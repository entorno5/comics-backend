package com.comics.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Comic entity representing a comic book in the system.
 * Stores comic information and metadata.
 */
@Document(collection = "comics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comic {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title;

    private int number;

    private String publisher;

    private double price;

    private String description;

    private int stock = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Boolean active = true;

    /**
     * Constructor con los 4 parámetros principales
     */
    public Comic(String title, int number, String publisher, double price) {
        this.title = title;
        this.number = number;
        this.publisher = publisher;
        this.price = price;
        this.stock = 0;
        this.active = true;
    }
}

