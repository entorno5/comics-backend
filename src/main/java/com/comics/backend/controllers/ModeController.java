package com.comics.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mode")
@RequiredArgsConstructor
@Tag(name = "Mode", description = "Active runtime profile")
public class ModeController {

    private final Environment env;

    @GetMapping
    @Operation(summary = "Returns the active Spring profile (mock | default)")
    public ResponseEntity<Map<String, String>> getMode() {
        boolean isMock = Arrays.asList(env.getActiveProfiles()).contains("mock");
        return ResponseEntity.ok(Map.of("profile", isMock ? "mock" : "default"));
    }
}
