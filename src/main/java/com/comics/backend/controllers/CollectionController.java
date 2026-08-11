package com.comics.backend.controllers;

import com.comics.backend.dto.CollectionPageResponse;
import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateCollectionDTO;
import com.comics.backend.services.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<List<CollectionResponseDTO>> getCollections(
            @RequestParam(defaultValue = "false") boolean owned) {
        log.debug("GET /api/v1/collections?owned={}", owned);
        return ResponseEntity.ok(collectionService.getAllCollections(owned));
    }

    /**
     * Returns a page of collections ordered alphabetically.
     */
    @GetMapping("/page")
    @Operation(
            summary = "Get collections page",
            description = "Retrieve a page of collections ordered alphabetically, with issue counts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collections page retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionPageResponse> getCollectionsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean owned) {
        log.debug("GET /api/v1/collections/page?page={}&size={}&owned={}", page, size, owned);
        return ResponseEntity.ok(collectionService.getCollectionsPage(page, size, owned));
    }

    /**
     * Returns a single collection by id.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get collection by ID",
            description = "Retrieve a single collection with its issue count, description, ongoing status and completion goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collection retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Collection not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionResponseDTO> getCollection(@PathVariable String id) {
        log.debug("GET /api/v1/collections/{}", id);
        return ResponseEntity.ok(collectionService.getCollection(id));
    }

    /**
     * Returns the comics of a collection ordered by issue number ascending.
     */
    @GetMapping("/{id}/comics")
    @Operation(
            summary = "Get comics of a collection",
            description = "Retrieve all comics belonging to a collection, ordered by issue number ascending")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Collection not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ComicResponseDTO>> getCollectionComics(@PathVariable String id) {
        log.debug("GET /api/v1/collections/{}/comics", id);
        return ResponseEntity.ok(collectionService.getCollectionComics(id));
    }

    /**
     * Creates a new collection (admin only).
     */
    @PostMapping
    @Operation(summary = "Create collection", description = "Create a new collection. Requires ADMIN role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Collection created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "409", description = "Collection name already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionResponseDTO> createCollection(@Valid @RequestBody CreateCollectionDTO dto) {
        log.info("POST /api/v1/collections - name: {}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.createCollection(dto));
    }

    /**
     * Updates an existing collection (admin only).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update collection", description = "Update an existing collection. Renaming propagates to its comics. Requires ADMIN role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collection updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Collection not found"),
            @ApiResponse(responseCode = "409", description = "Collection name already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionResponseDTO> updateCollection(
            @PathVariable String id,
            @Valid @RequestBody CreateCollectionDTO dto) {
        log.info("PUT /api/v1/collections/{}", id);
        return ResponseEntity.ok(collectionService.updateCollection(id, dto));
    }

    /**
     * Deletes a collection without comics (admin only).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete collection", description = "Delete a collection. Blocked while it still has comics. Requires ADMIN role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Collection deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Collection still has comics"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Collection not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteCollection(@PathVariable String id) {
        log.info("DELETE /api/v1/collections/{}", id);
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Flips the wantToComplete flag of a collection and returns the updated resource.
     */
    @PatchMapping("/{id}/want-to-complete")
    @Operation(
            summary = "Toggle wantToComplete",
            description = "Flip the wantToComplete flag of a collection: true = 'por completar', false = 'no coleccionar'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Collection updated successfully"),
            @ApiResponse(responseCode = "404", description = "Collection not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionResponseDTO> toggleWantToComplete(@PathVariable String id) {
        log.debug("PATCH /api/v1/collections/{}/want-to-complete", id);
        return ResponseEntity.ok(collectionService.toggleWantToComplete(id));
    }
}
