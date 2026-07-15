package com.comics.backend.controllers;

import com.comics.backend.dto.ComicPageResponse;
import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateComicDTO;
import com.comics.backend.services.ComicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST Controller for comic management endpoints.
 * Provides CRUD operations for comics.
 */
@RestController
@RequestMapping("/api/v1/comics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comics", description = "Comic management endpoints")
public class ComicController {

    private final ComicService comicService;
    private final Environment env;

    /**
     * Get all comics with pagination
     */
    @GetMapping
    @Operation(summary = "Get all comics", description = "Retrieve all comics with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comics retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicPageResponse> getComics(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by title (case-insensitive, partial match)")
            @RequestParam(required = false) String q) {

        log.debug("Getting comics with page: {}, size: {}, q: {}", page, size, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));
        Page<ComicResponseDTO> comics = comicService.getAllComics(pageable, q);
        boolean isMock = Arrays.asList(env.getActiveProfiles()).contains("mock");
        return ResponseEntity.ok(new ComicPageResponse(
                comics.getContent(),
                comics.getTotalElements(),
                comics.getTotalPages(),
                isMock));
    }

    /**
     * Create a new comic
     */
    @PostMapping
    @Operation(summary = "Create a new comic", description = "Create a new comic with provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comic created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Comic already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicResponseDTO> createComic(@Valid @RequestBody CreateComicDTO createComicDTO) {
        log.info("Creating new comic with title: {}", createComicDTO.getTitle());
        ComicResponseDTO savedComic = comicService.createComic(createComicDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComic);
    }

    /**
     * Get comic by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get comic by ID", description = "Retrieve a specific comic by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comic found"),
            @ApiResponse(responseCode = "404", description = "Comic not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicResponseDTO> getComicById(
            @Parameter(description = "Comic ID")
            @PathVariable String id) {
        
        log.debug("Getting comic by ID: {}", id);
        ComicResponseDTO comic = comicService.getComicById(id);
        return ResponseEntity.ok(comic);
    }

    /**
     * Get all comics by title (may return multiple)
     */
    @GetMapping("/search")
    @Operation(summary = "Search comics by title", description = "Retrieve all comics matching a search term (case-insensitive, partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comics found"),
            @ApiResponse(responseCode = "404", description = "Comics not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ComicResponseDTO>> searchComicsByTitle(
            @Parameter(description = "Comic title search term")
            @RequestParam String title) {
        
        log.debug("Searching comics by title: {}", title);
        List<ComicResponseDTO> comics = comicService.searchComicsByTitle(title);
        return ResponseEntity.ok(comics);
    }

    /**
     * Get first comic by title
     */
    @GetMapping("/title/{title}")
    @Operation(summary = "Get first comic by title", description = "Retrieve the first comic with a specific title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comic found"),
            @ApiResponse(responseCode = "404", description = "Comic not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicResponseDTO> getComicByTitle(
            @Parameter(description = "Comic title")
            @PathVariable String title) {
        
        log.debug("Getting first comic by title: {}", title);
        ComicResponseDTO comic = comicService.getComicByTitle(title);
        return ResponseEntity.ok(comic);
    }

    /**
     * Update comic
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update comic", description = "Update an existing comic's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comic updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Comic not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate comic title"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicResponseDTO> updateComic(
            @Parameter(description = "Comic ID")
            @PathVariable String id,
            @Valid @RequestBody CreateComicDTO updateDTO) {
        
        log.info("Updating comic with ID: {}", id);
        ComicResponseDTO updatedComic = comicService.updateComic(id, updateDTO);
        return ResponseEntity.ok(updatedComic);
    }

    /**
     * Delete comic by ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comic", description = "Delete a comic by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comic deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comic not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteComic(
            @Parameter(description = "Comic ID")
            @PathVariable String id) {
        
        log.info("Deleting comic with ID: {}", id);
        comicService.deleteComic(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivate comic
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate comic", description = "Deactivate a comic (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comic deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Comic not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicResponseDTO> deactivateComic(
            @Parameter(description = "Comic ID")
            @PathVariable String id) {
        
        log.info("Deactivating comic with ID: {}", id);
        ComicResponseDTO deactivatedComic = comicService.deactivateComic(id);
        return ResponseEntity.ok(deactivatedComic);
    }

    /**
     * Update comic stock
     */
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update comic stock", description = "Update the stock quantity for a comic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Comic not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ComicResponseDTO> updateStock(
            @Parameter(description = "Comic ID")
            @PathVariable String id,
            @Parameter(description = "New stock quantity")
            @RequestParam int stock) {
        
        log.info("Updating stock for comic with ID: {} to {}", id, stock);
        ComicResponseDTO updatedComic = comicService.updateComicStock(id, stock);
        return ResponseEntity.ok(updatedComic);
    }
}

