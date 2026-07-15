package com.comics.backend.services;

import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.Collection;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.ComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    /**
     * Returns all collections ordered alphabetically, each enriched with its issue count.
     */
    public List<CollectionResponseDTO> getAllCollections() {
        log.debug("Fetching all collections");
        return collectionRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(col -> {
                    long count = comicRepository.countByCollectionName(col.getName());
                    return entityMapper.toCollectionResponseDTO(col, count);
                })
                .collect(Collectors.toList());
    }
}
