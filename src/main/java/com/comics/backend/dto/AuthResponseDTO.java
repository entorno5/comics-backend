package com.comics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String nickname;
    private String name;
    private Set<String> roles;
}
