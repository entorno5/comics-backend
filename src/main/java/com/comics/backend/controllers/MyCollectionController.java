package com.comics.backend.controllers;

import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.services.UserCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/my/collections")
@RequiredArgsConstructor
@Tag(name = "My Collections", description = "Collections derived from the user's owned comics")
public class MyCollectionController {

    private final UserCollectionService userCollectionService;

    @GetMapping
    @Operation(summary = "Get my collections", description = "All collections the user has at least one owned comic from")
    public ResponseEntity<List<CollectionResponseDTO>> getMyCollections(Principal principal) {
        return ResponseEntity.ok(userCollectionService.getMyCollections(principal.getName()));
    }
}
