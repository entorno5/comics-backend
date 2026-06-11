package com.comics.backend.services;

import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateComicDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.Comic;
import com.comics.backend.repository.ComicRepository;
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

        // Check if comic with same title and number already exists
        if (comicRepository.findByTitleAndNumber(createComicDTO.getTitle(), createComicDTO.getNumber()).isPresent()) {
            log.warn("Comic with title '{}' and number {} already exists", createComicDTO.getTitle(), createComicDTO.getNumber());
            throw new DuplicateResourceException("Comic", "title", createComicDTO.getTitle());
        }

        // Create comic from DTO
        Comic comic = entityMapper.toComicEntity(createComicDTO);

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
        
        return entityMapper.toComicResponseDTO(comic);
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
        
        // Update fields if provided
        if (StringUtils.isNotBlank(updateDTO.getTitle()) && !updateDTO.getTitle().equals(comic.getTitle())) {
            // Check for duplicate title and number combination
            if (comicRepository.findByTitleAndNumber(updateDTO.getTitle(), updateDTO.getNumber()).isPresent()) {
                throw new DuplicateResourceException("Comic", "title", updateDTO.getTitle());
            }
            comic.setTitle(updateDTO.getTitle());
        }
        
        if (updateDTO.getNumber() > 0) {
            comic.setNumber(updateDTO.getNumber());
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
        
        comicRepository.deleteById(id);
        log.info("Comic deleted successfully with ID: {}", id);
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

