package com.comics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserComicPageResponse {
    private List<UserComicResponseDTO> content;
    private long totalElements;
    private int totalPages;
    private boolean mock;
}
