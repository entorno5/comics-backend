package com.comics.backend.services;

import com.comics.backend.dto.CreateUserDTO;
import com.comics.backend.dto.UserResponseDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.User;
import com.comics.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for user management operations.
 * Handles business logic for user CRUD operations and authentication.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;

    /**
     * Get all users with pagination
     */
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(entityMapper::toUserResponseDTO);
    }

    /**
     * Get all users (without pagination)
     */
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(entityMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new user
     */
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        log.info("Creating new user with nickname: {}", createUserDTO.getNickname());
        
        // Validate input
        if (StringUtils.isAnyBlank(createUserDTO.getNickname(), createUserDTO.getMail())) {
            throw new IllegalArgumentException("Nickname and email cannot be blank");
        }

        // Check if user already exists
        if (userRepository.findByNickname(createUserDTO.getNickname()).isPresent()) {
            log.warn("User with nickname '{}' already exists", createUserDTO.getNickname());
            throw new DuplicateResourceException("User", "nickname", createUserDTO.getNickname());
        }

        if (userRepository.findByMail(createUserDTO.getMail()).isPresent()) {
            log.warn("User with email '{}' already exists", createUserDTO.getMail());
            throw new DuplicateResourceException("User", "email", createUserDTO.getMail());
        }

        // Create user from DTO
        User user = entityMapper.toUserEntity(createUserDTO);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role
        if (user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton("USER"));
        }

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return entityMapper.toUserResponseDTO(savedUser);
    }

    /**
     * Get user by nickname
     */
    public UserResponseDTO getUserByNickname(String nickname) {
        log.debug("Fetching user by nickname: {}", nickname);
        
        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException("Nickname cannot be blank");
        }
        
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> {
                    log.warn("User not found with nickname: {}", nickname);
                    return new ResourceNotFoundException("User", nickname);
                });
        
        return entityMapper.toUserResponseDTO(user);
    }

    /**
     * Get user by ID
     */
    public UserResponseDTO getUserById(String id) {
        log.debug("Fetching user by ID: {}", id);
        
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", id);
                });
        
        return entityMapper.toUserResponseDTO(user);
    }

    /**
     * Get user by email (internal use only)
     */
    public User getUserByMail(String mail) {
        log.debug("Fetching user by mail: {}", mail);
        
        if (StringUtils.isBlank(mail)) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        
        return userRepository.findByMail(mail)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", mail);
                    return new ResourceNotFoundException("User", mail);
                });
    }

    /**
     * Update user
     */
    public UserResponseDTO updateUser(String id, CreateUserDTO updateDTO) {
        log.info("Updating user with ID: {}", id);
        
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        // Update fields if provided
        if (StringUtils.isNotBlank(updateDTO.getName())) {
            user.setName(updateDTO.getName());
        }
        
        if (StringUtils.isNotBlank(updateDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", id);
        
        return entityMapper.toUserResponseDTO(updatedUser);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(String id) {
        log.info("Deleting user with ID: {}", id);
        
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
        
        if (!userRepository.existsById(id)) {
            log.warn("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User", id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Deactivate user (soft delete)
     */
    public UserResponseDTO deactivateUser(String id) {
        log.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        
        log.info("User deactivated successfully with ID: {}", id);
        return entityMapper.toUserResponseDTO(updatedUser);
    }
}
