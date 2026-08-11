package com.comics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserComicResponseDTO {
    private String id;
    private ComicResponseDTO comic;
    private int units;
    private LocalDateTime addedAt;
}
