package com.comics.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")

public class User {

    @Id
    private String id; // Mongo genera ObjectId automáticamente

    @Indexed(unique = true) // Evita duplicados
    private String nickname;
    private String name;
    private String password;

    @Indexed(unique = true) // Evita duplicados
    private String mail;

    private Set<String> roles = new HashSet<>(); // ADMIN, USER, etc.

    public User() {}

    public User(String nickname, String name, String password, String mail, Set<String> roles) {
        this.nickname = nickname;
        this.name = name;
        this.password = password;
        this.mail = mail;
        this.roles = roles;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}