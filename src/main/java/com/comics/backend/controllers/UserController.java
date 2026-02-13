package com.comics.backend.controllers;

import org.springframework.web.bind.annotation.*;
import com.comics.backend.services.UserService;
import com.comics.backend.dto.UserResponseDTO;
import com.comics.backend.models.User;

import org.springframework.lang.NonNull;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { 
        this.userService = userService; 
    }

    @GetMapping
    public List<UserResponseDTO> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponseDTO addUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/nickname/{nickname}")
    public UserResponseDTO getUserByNickname(@PathVariable String nickname) {
        return userService.getUserByNickname(nickname);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @NonNull String id) {
        userService.deleteUser(id);
    }
}