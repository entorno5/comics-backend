package com.comics.backend.services;

import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateComicDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.Comic;
import com.comics.backend.repository.ComicRepository;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.UserComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for comic management operations.
 * Handles business logic for comic CRUD operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ComicService {

    private final ComicRepository comicRepository;
    private final CollectionRepository collectionRepository;
    private final UserComicRepository userComicRepository;
    private final EntityMapper entityMapper;

    /**
     * Get all comics with pagination
     */
    public Page<ComicResponseDTO> getAllComics(Pageable pageable) {
        log.debug("Fetching all comics with pagination: {}", pageable);
        return comicRepository.findAll(pageable)
                .map(entityMapper::toComicResponseDTO);
    }

    /**
     * Get all comics with pagination and optional title filter
     */
    public Page<ComicResponseDTO> getAllComics(Pageable pageable, String q) {
        if (q == null || q.isBlank()) {
            return getAllComics(pageable);
        }
        log.debug("Fetching comics with filter q='{}' and pagination: {}", q, pageable);
        return comicRepository.findByTitleContainsIgnoreCasePaged(q.trim(), pageable)
                .map(entityMapper::toComicResponseDTO);
    }

    /**
     * Get comics with pagination, optional title filter and optional owned filter.
     * With ownedOnly=true only comics the collector owns are returned (null owned counts as false).
     */
    public Page<ComicResponseDTO> getAllComics(Pageable pageable, String q, boolean ownedOnly) {
        if (!ownedOnly) {
            return getAllComics(pageable, q);
        }
        if (q == null || q.isBlank()) {
            log.debug("Fetching owned comics with pagination: {}", pageable);
            return comicRepository.findByOwnedTrue(pageable)
                    .map(entityMapper::toComicResponseDTO);
        }
        log.debug("Fetching owned comics with filter q='{}' and pagination: {}", q, pageable);
        return comicRepository.findByTitleContainsIgnoreCaseAndOwnedTruePaged(q.trim(), pageable)
                .map(entityMapper::toComicResponseDTO);
    }

    /**
     * Get all comics (without pagination)
     */
    public List<ComicResponseDTO> getAllComics() {
        log.debug("Fetching all comics");
        return comicRepository.findAll()
                .stream()
                .map(entityMapper::toComicResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new comic
     */
    public ComicResponseDTO createComic(CreateComicDTO createComicDTO) {
        log.info("Creating new comic with title: {}", createComicDTO.getTitle());
        
        // Validate input
        if (StringUtils.isBlank(createComicDTO.getTitle())) {
            throw new IllegalArgumentException("Comic title cannot be blank");
        }

        String collectionName = validateCollection(createComicDTO.getCollectionName());

        // Check if comic with same title and number already exists
        if (comicRepository.findByTitleAndNumber(createComicDTO.getTitle(), createComicDTO.getNumber()).isPresent()) {
            log.warn("Comic with title '{}' and number {} already exists", createComicDTO.getTitle(), createComicDTO.getNumber());
            throw new DuplicateResourceException(String.format("Comic with title '%s' and number %d already exists", createComicDTO.getTitle(), createComicDTO.getNumber()));
        }

        // Create comic from DTO
        Comic comic = entityMapper.toComicEntity(createComicDTO);
        comic.setCollectionName(collectionName);

        // Save comic
        Comic savedComic = comicRepository.save(comic);
        log.info("Comic created successfully with ID: {}", savedComic.getId());

        return entityMapper.toComicResponseDTO(savedComic);
    }

    /**
     * Get comic by ID
     */
    public ComicResponseDTO getComicById(String id) {
        log.debug("Fetching comic by ID: {}", id);
        
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Comic ID cannot be blank");
        }
        
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comic not found with ID: {}", id);
                    return new ResourceNotFoundException("Comic", id);
                });

        ComicResponseDTO dto = entityMapper.toComicResponseDTO(comic);
        if (comic.getCollectionName() != null) {
            collectionRepository.findByName(comic.getCollectionName())
                    .ifPresent(col -> dto.setCollectionId(col.getId()));
        }
        return dto;
    }

    /**
     * Search comics by title (case-insensitive, partial match)
     */
    public List<ComicResponseDTO> searchComicsByTitle(String titleSearch) {
        log.debug("Searching comics by title: {}", titleSearch);
        
        if (StringUtils.isBlank(titleSearch)) {
            throw new IllegalArgumentException("Search term cannot be blank");
        }
        
        List<Comic> comics = comicRepository.findByTitleContainsIgnoreCase(titleSearch);
        if (comics.isEmpty()) {
            log.warn("No comics found matching title: {}", titleSearch);
            throw new ResourceNotFoundException("Comic", titleSearch);
        }
        
        return comics.stream()
                .map(entityMapper::toComicResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get comic by title and number
     */
    public ComicResponseDTO getComicByTitle(String title) {
        log.debug("Fetching first comic by title: {}", title);
        
        List<ComicResponseDTO> comics = searchComicsByTitle(title);
        return comics.get(0);
    }

    /**
     * Update comic
     */
    public ComicResponseDTO updateComic(String id, CreateComicDTO updateDTO) {
        log.info("Updating comic with ID: {}", id);
        
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Comic ID cannot be blank");
        }
        
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic", id));

        String title = StringUtils.isNotBlank(updateDTO.getTitle())
                ? updateDTO.getTitle().trim()
                : comic.getTitle();
        int number = updateDTO.getNumber() > 0 ? updateDTO.getNumber() : comic.getNumber();
        String collectionName = validateCollection(
                StringUtils.isNotBlank(updateDTO.getCollectionName())
                        ? updateDTO.getCollectionName()
                        : comic.getCollectionName());

        comicRepository.findByTitleAndNumber(title, number)
                .filter(existing -> !id.equals(existing.getId()))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            String.format("Comic with title '%s' and number %d already exists", title, number));
                });

        // Update fields if provided
        if (StringUtils.isNotBlank(updateDTO.getTitle())) {
            comic.setTitle(title);
        }
        
        if (updateDTO.getNumber() > 0) {
            comic.setNumber(number);
        }
        
        if (StringUtils.isNotBlank(updateDTO.getPublisher())) {
            comic.setPublisher(updateDTO.getPublisher());
        }
        
        if (updateDTO.getPrice() > 0) {
            comic.setPrice(updateDTO.getPrice());
        }
        
        if (StringUtils.isNotBlank(updateDTO.getDescription())) {
            comic.setDescription(updateDTO.getDescription());
        }
        
        if (updateDTO.getStock() >= 0) {
            comic.setStock(updateDTO.getStock());
        }

        comic.setCollectionName(collectionName);

        if (updateDTO.getPublishedDate() != null) {
            comic.setPublishedDate(updateDTO.getPublishedDate());
        }

        if (updateDTO.getImageUrl() != null) {
            comic.setImageUrl(StringUtils.isBlank(updateDTO.getImageUrl()) ? null : updateDTO.getImageUrl());
        }

        Comic updatedComic = comicRepository.save(comic);
        log.info("Comic updated successfully with ID: {}", id);
        
        return entityMapper.toComicResponseDTO(updatedComic);
    }

    /**
     * Delete comic by ID
     */
    public void deleteComic(String id) {
        log.info("Deleting comic with ID: {}", id);
        
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Comic ID cannot be blank");
        }
        
        if (!comicRepository.existsById(id)) {
            log.warn("Comic not found with ID: {}", id);
            throw new ResourceNotFoundException("Comic", id);
        }
        
        userComicRepository.deleteByComicId(id);
        comicRepository.deleteById(id);
        log.info("Comic deleted successfully with ID: {}", id);
    }

    private String validateCollection(String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("Comic collection cannot be blank");
        }

        String normalizedName = collectionName.trim();
        if (collectionRepository.findByName(normalizedName).isEmpty()) {
            throw new ResourceNotFoundException("Collection", normalizedName);
        }
        return normalizedName;
    }

    /**
     * Deactivate comic (soft delete)
     */
    public ComicResponseDTO deactivateComic(String id) {
        log.info("Deactivating comic with ID: {}", id);
        
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic", id));
        
        comic.setActive(false);
        Comic updatedComic = comicRepository.save(comic);
        
        log.info("Comic deactivated successfully with ID: {}", id);
        return entityMapper.toComicResponseDTO(updatedComic);
    }

    /**
     * Flips the owned flag of a comic.
     * A null flag is treated as false, so the first toggle always yields true.
     *
     * @param id comic identifier
     * @return the updated comic
     * @throws ResourceNotFoundException if no comic exists with the given id
     */
    public ComicResponseDTO toggleOwned(String id) {
        log.info("Toggling owned for comic with ID: {}", id);

        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic", id));

        comic.setOwned(!Boolean.TRUE.equals(comic.getOwned()));
        Comic updatedComic = comicRepository.save(comic);

        log.info("Comic owned flag updated successfully with ID: {}", id);
        return entityMapper.toComicResponseDTO(updatedComic);
    }

    /**
     * Update comic stock
     */
    public ComicResponseDTO updateComicStock(String id, int newStock) {
        log.info("Updating stock for comic with ID: {} to {}", id, newStock);
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic", id));
        
        comic.setStock(newStock);
        Comic updatedComic = comicRepository.save(comic);
        
        log.info("Comic stock updated successfully with ID: {}", id);
        return entityMapper.toComicResponseDTO(updatedComic);
    }
}
