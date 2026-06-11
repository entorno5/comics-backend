package com.comics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Comic response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComicResponseDTO {
    private String id;
    private String title;
    private int number;
    private String publisher;
    private double price;
    private String description;
    private int stock;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
