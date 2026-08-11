package com.comics.backend.controllers;

import com.comics.backend.dto.AddMyComicDTO;
import com.comics.backend.dto.UpdateMyComicDTO;
import com.comics.backend.dto.UserComicPageResponse;
import com.comics.backend.dto.UserComicResponseDTO;
import com.comics.backend.services.UserComicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/my/comics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "My Comics", description = "Per-user comic collection endpoints")
public class MyComicController {

    private final UserComicService userComicService;
    private final Environment env;

    @GetMapping
    @Operation(summary = "Get my comics", description = "Paginated list of comics in the authenticated user's collection")
    public ResponseEntity<UserComicPageResponse> getMyComics(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q) {

        boolean isMock = Arrays.asList(env.getActiveProfiles()).contains("mock");
        UserComicPageResponse result = userComicService.getMyComicsPage(principal.getName(), page, size, q, isMock);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{comicId}")
    @Operation(summary = "Get a single comic from my collection")
    public ResponseEntity<UserComicResponseDTO> getMyComic(
            Principal principal,
            @PathVariable String comicId) {

        return userComicService.getMyComic(principal.getName(), comicId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{comicId}")
    @Operation(summary = "Add comic to my collection")
    public ResponseEntity<UserComicResponseDTO> addMyComic(
            Principal principal,
            @PathVariable String comicId,
            @Valid @RequestBody(required = false) AddMyComicDTO dto) {

        int units = (dto != null) ? dto.getUnits() : 1;
        UserComicResponseDTO result = userComicService.addMyComic(principal.getName(), comicId, units);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{comicId}")
    @Operation(summary = "Update units of a comic in my collection")
    public ResponseEntity<UserComicResponseDTO> updateMyComic(
            Principal principal,
            @PathVariable String comicId,
            @Valid @RequestBody UpdateMyComicDTO dto) {

        UserComicResponseDTO result = userComicService.updateMyComic(principal.getName(), comicId, dto.getUnits());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{comicId}")
    @Operation(summary = "Remove comic from my collection")
    public ResponseEntity<Void> removeMyComic(
            Principal principal,
            @PathVariable String comicId) {

        userComicService.removeMyComic(principal.getName(), comicId);
        return ResponseEntity.noContent().build();
    }
}
