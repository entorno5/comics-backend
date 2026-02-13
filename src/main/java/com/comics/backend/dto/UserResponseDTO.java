package com.comics.backend.dto;

import java.util.Set;

public class UserResponseDTO {
    private String id;
    private String nickname;
    private String name;
    private String mail;
    private Set<String> roles;

    public UserResponseDTO(String id, String nickname, String name, String mail, Set<String> roles) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.mail = mail;
        this.roles = roles;
    }

    // Getters
    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getName() { return name; }
    public String getMail() { return mail; }
    public Set<String> getRoles() { return roles; }
}