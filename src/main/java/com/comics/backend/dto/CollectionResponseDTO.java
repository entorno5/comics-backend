package com.comics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Collection response.
 * Includes computed issueCount from associated comics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponseDTO {
    private String id;
    private String name;
    private String publisher;
    private Boolean ongoingCollection;
    private Boolean wantToComplete;
    private long issueCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
