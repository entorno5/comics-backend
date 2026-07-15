package com.comics.backend.controllers;

import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.services.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for collection management endpoints.
 * A collection groups all issues belonging to the same comic series.
 */
@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Collections", description = "Comic series/collection management endpoints")
public class CollectionController {

    private final CollectionService collectionService;

    /**
     * Returns all collections alphabetically, each with its computed issue count.
     */
    @GetMapping
    @Operation(
            summary = "Get all collections",
            description = "Retrieve all comic series/collections with their issue count, ongoing status and completion goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collections retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CollectionResponseDTO>> getCollections() {
        log.debug("GET /api/v1/collections");
        return ResponseEntity.ok(collectionService.getAllCollections());
    }
}
