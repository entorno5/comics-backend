package com.comics.backend.dto;

import java.util.List;

/**
 * Wrapper for the paginated comics list.
 * Includes a {@code mock} flag that is {@code true} when the application
 * is running with the "mock" Spring profile (no real MongoDB).
 */
public record ComicPageResponse(
        List<ComicResponseDTO> content,
        long totalElements,
        int totalPages,
        boolean mock
) {}
