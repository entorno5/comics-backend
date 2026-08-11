package com.comics.backend.services;

import com.comics.backend.dto.CollectionPageResponse;
import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateCollectionDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.exceptions.ValidationException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.Collection;
import com.comics.backend.models.Comic;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.ComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for collection management.
 * Enriches each collection with its computed issue count from the comic catalog.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ComicRepository comicRepository;
    private final EntityMapper entityMapper;

    /** Maps a collection to its DTO, computing total and owned issue counts. */
    private CollectionResponseDTO toDto(Collection col) {
        long issueCount = comicRepository.countByCollectionName(col.getName());
        long ownedCount = comicRepository.countByCollectionNameAndOwnedTrue(col.getName());
        return entityMapper.toCollectionResponseDTO(col, issueCount, ownedCount);
    }

    /**
     * Returns all collections ordered alphabetically, each enriched with its issue counts.
     * With ownedOnly=true, only collections with at least one owned issue are returned.
     */
    public List<CollectionResponseDTO> getAllCollections(boolean ownedOnly) {
        log.debug("Fetching all collections (ownedOnly={})", ownedOnly);
        return collectionRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::toDto)
                .filter(dto -> !ownedOnly || dto.getOwnedCount() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Returns a page of collections ordered alphabetically, each enriched with its issue counts.
     * With ownedOnly=true, only collections with at least one owned issue are considered.
     * Sorting, filtering and slicing happen in memory so behaviour is identical in mock and MongoDB profiles.
     */
    public CollectionPageResponse getCollectionsPage(int page, int size, boolean ownedOnly) {
        log.debug("Fetching collections page {} (size {}, ownedOnly={})", page, size, ownedOnly);
        List<Collection> all = collectionRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .filter(col -> !ownedOnly || comicRepository.countByCollectionNameAndOwnedTrue(col.getName()) > 0)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) all.size() / size);
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());

        List<CollectionResponseDTO> content = all.subList(from, to)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new CollectionPageResponse(content, all.size(), totalPages);
    }

    /**
     * Returns a single collection by id, enriched with its issue count.
     *
     * @throws ResourceNotFoundException if no collection exists with the given id
     */
    public CollectionResponseDTO getCollection(String id) {
        log.debug("Fetching collection {}", id);
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        return toDto(collection);
    }

    /**
     * Returns the comics belonging to a collection, ordered by issue number ascending.
     *
     * @throws ResourceNotFoundException if no collection exists with the given id
     */
    public List<ComicResponseDTO> getCollectionComics(String id) {
        log.debug("Fetching comics for collection {}", id);
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        return comicRepository.findByCollectionName(collection.getName())
                .stream()
                .sorted(Comparator.comparingInt(c -> c.getNumber()))
                .map(entityMapper::toComicResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Flips the wantToComplete flag of a collection.
     * A null flag is treated as false, so the first toggle always yields true.
     *
     * @param id collection identifier
     * @return the updated collection with its issue count
     * @throws ResourceNotFoundException if no collection exists with the given id
     */
    public CollectionResponseDTO toggleWantToComplete(String id) {
        log.debug("Toggling wantToComplete for collection {}", id);
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        collection.setWantToComplete(!Boolean.TRUE.equals(collection.getWantToComplete()));
        Collection saved = collectionRepository.save(collection);

        return toDto(saved);
    }

    /**
     * Creates a new collection.
     *
     * @throws DuplicateResourceException if a collection with the same name already exists
     */
    public CollectionResponseDTO createCollection(CreateCollectionDTO dto) {
        log.info("Creating collection '{}'", dto.getName());

        if (collectionRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    String.format("Collection with name '%s' already exists", dto.getName()));
        }

        Collection collection = new Collection(
                dto.getName(),
                dto.getPublisher(),
                Boolean.TRUE.equals(dto.getOngoingCollection()),
                Boolean.TRUE.equals(dto.getWantToComplete()));
        collection.setDescription(dto.getDescription());

        Collection saved = collectionRepository.save(collection);
        log.info("Collection created successfully with ID: {}", saved.getId());
        return toDto(saved);
    }

    /**
     * Updates an existing collection. If the name changes, the change is propagated
     * to the collectionName of every comic that belongs to it, so the by-name
     * association is preserved.
     *
     * @throws ResourceNotFoundException  if no collection exists with the given id
     * @throws DuplicateResourceException if the new name is already taken by another collection
     */
    public CollectionResponseDTO updateCollection(String id, CreateCollectionDTO dto) {
        log.info("Updating collection with ID: {}", id);

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        String oldName = collection.getName();
        boolean renamed = !oldName.equals(dto.getName());

        if (renamed && collectionRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    String.format("Collection with name '%s' already exists", dto.getName()));
        }

        collection.setName(dto.getName());
        collection.setPublisher(dto.getPublisher());
        collection.setDescription(dto.getDescription());
        collection.setOngoingCollection(Boolean.TRUE.equals(dto.getOngoingCollection()));
        collection.setWantToComplete(Boolean.TRUE.equals(dto.getWantToComplete()));

        Collection saved = collectionRepository.save(collection);

        if (renamed) {
            List<Comic> comics = comicRepository.findByCollectionName(oldName);
            comics.forEach(c -> c.setCollectionName(saved.getName()));
            comicRepository.saveAll(comics);
            log.info("Propagated collection rename '{}' -> '{}' to {} comics", oldName, saved.getName(), comics.size());
        }

        return toDto(saved);
    }

    /**
     * Deletes a collection. Deletion is blocked while the collection still has comics,
     * to avoid orphan issues in the catalog.
     *
     * @throws ResourceNotFoundException if no collection exists with the given id
     * @throws ValidationException       if the collection still has comics
     */
    public void deleteCollection(String id) {
        log.info("Deleting collection with ID: {}", id);

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        long count = comicRepository.countByCollectionName(collection.getName());
        if (count > 0) {
            throw new ValidationException(
                    String.format("Collection '%s' still has %d comics. Delete or reassign them first.",
                            collection.getName(), count));
        }

        collectionRepository.deleteById(id);
        log.info("Collection deleted successfully with ID: {}", id);
    }
}
