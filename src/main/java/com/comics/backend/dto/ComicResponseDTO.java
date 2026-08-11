package com.comics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
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
    private String collectionName;
    private String collectionId;
    private int number;
    private String publisher;
    private double price;
    private String description;
    private int stock;
    private String imageUrl;
    private Boolean owned;
    private Boolean active;
    private LocalDate publishedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
