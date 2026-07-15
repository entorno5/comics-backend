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
 * Collection entity representing a comic book series.
 * Groups individual Comic issues under a single series name.
 * Holds collection-level metadata such as ongoing status and completion goal.
 */
@Document(collection = "collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {

    @Id
    private String id;

    /** Unique name of the series (e.g., "Batman", "The Amazing Spider-Man"). */
    @Indexed(unique = true)
    private String name;

    private String publisher;

    /** True if the series is still being published (new issues coming out). */
    private Boolean ongoingCollection;

    /** True if the collector wants to acquire all issues in this series. */
    private Boolean wantToComplete;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Collection(String name, String publisher, Boolean ongoingCollection, Boolean wantToComplete) {
        this.name = name;
        this.publisher = publisher;
        this.ongoingCollection = ongoingCollection;
        this.wantToComplete = wantToComplete;
    }
}
