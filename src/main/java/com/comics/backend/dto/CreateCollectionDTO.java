package com.comics.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a collection.
 * Includes validation constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCollectionDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    private String name;

    @NotBlank(message = "Publisher is required")
    @Size(min = 2, max = 100, message = "Publisher must be between 2 and 100 characters")
    private String publisher;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Boolean ongoingCollection;

    private Boolean wantToComplete;
}
