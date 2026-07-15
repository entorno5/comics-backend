package com.comics.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO for creating a new comic.
 * Includes validation constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateComicDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @Size(max = 200, message = "Collection name cannot exceed 200 characters")
    private String collectionName;

    @Min(value = 1, message = "Number must be at least 1")
    @Max(value = 999999, message = "Number cannot exceed 999999")
    private int number;

    @NotBlank(message = "Publisher is required")
    @Size(min = 2, max = 100, message = "Publisher must be between 2 and 100 characters")
    private String publisher;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private double price;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    private LocalDate publishedDate;
}
