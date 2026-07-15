package com.comics.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Comic entity representing a comic book in the system.
 * Stores comic information and metadata.
 */
@Document(collection = "comics")
@CompoundIndexes({
    @CompoundIndex(name = "title_number_unique", def = "{'title': 1, 'number': 1}", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comic {

    @Id
    private String id;

    private String title;

    /** Name of the series/collection this issue belongs to (e.g., "Batman"). */
    private String collectionName;

    private int number;

    private String publisher;

    private double price;

    private String description;

    private int stock = 0;

    /** Publication date — used to sort comics from most recent to oldest. */
    private LocalDate publishedDate;

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
        this.collectionName = title;
        this.number = number;
        this.publisher = publisher;
        this.price = price;
        this.stock = 0;
        this.active = true;
    }
}

