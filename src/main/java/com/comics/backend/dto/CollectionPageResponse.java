package com.comics.backend.dto;

import java.util.List;

/**
 * Wrapper for the paginated collections list.
 */
public record CollectionPageResponse(
        List<CollectionResponseDTO> content,
        long totalElements,
        int totalPages
) {}
