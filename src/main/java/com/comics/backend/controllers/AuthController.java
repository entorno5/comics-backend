package com.comics.backend.controllers;

import com.comics.backend.dto.AuthResponseDTO;
import com.comics.backend.dto.LoginRequestDTO;
import com.comics.backend.models.User;
import com.comics.backend.repository.UserRepository;
import com.comics.backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getNickname(), request.getPassword()));

        String token = jwtUtil.generateToken(auth.getName());

        User user = userRepository.findByNickname(auth.getName()).orElseThrow();

        return ResponseEntity.ok(new AuthResponseDTO(token, user.getNickname(), user.getName()));
    }
}
