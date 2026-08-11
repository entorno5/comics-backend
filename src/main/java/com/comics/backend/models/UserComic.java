package com.comics.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_comics")
@CompoundIndex(name = "user_comic_unique", def = "{'userId': 1, 'comicId': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserComic {

    @Id
    private String id;

    private String userId;

    private String comicId;

    private int units = 1;

    @CreatedDate
    private LocalDateTime addedAt;
}
