package com.comics.backend.services;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.comics.backend.models.User;
import com.comics.backend.repository.UserRepository;
import com.comics.backend.dto.UserResponseDTO; // <-- import DTO
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los usuarios (como DTO)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(u -> new UserResponseDTO(u.getId(), u.getNickname(), u.getName(), u.getMail(), u.getRoles()))
            .collect(Collectors.toList());
    }

    // Crear un usuario
    public UserResponseDTO createUser(User user) {
        if (userRepository.findByNickname(user.getNickname()).isPresent()) {
            throw new RuntimeException("Nickname already exists");
        }

        if (userRepository.findByMail(user.getMail()).isPresent()) {
            throw new RuntimeException("Mail already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Por defecto rol USER
        if (user.getRoles().isEmpty()) {
            user.getRoles().add("USER");
        }

        User saved = userRepository.save(user);
        return new UserResponseDTO(saved.getId(), saved.getNickname(), saved.getName(), saved.getMail(), saved.getRoles());
    }

    // Obtener usuario por nickname
    public UserResponseDTO getUserByNickname(String nickname) {
        User u = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDTO(u.getId(), u.getNickname(), u.getName(), u.getMail(), u.getRoles());
    }

    // Obtener usuario por mail (sin DTO, interno)
    public User getUserByMail(String mail) {
        return userRepository.findByMail(mail)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Eliminar usuario
    public void deleteUser(@NonNull String id) {
        userRepository.deleteById(id);
    }
}