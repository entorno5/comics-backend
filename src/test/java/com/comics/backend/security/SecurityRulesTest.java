package com.comics.backend.security;

import com.comics.backend.models.Collection;
import com.comics.backend.models.Comic;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.ComicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the role-based security rules of the API.
 * Runs against the "mock" profile (in-memory repositories + seed data).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
@DisplayName("Security Rules Tests")
class SecurityRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ComicRepository comicRepository;

    private String anyCollectionId() {
        return collectionRepository.findAll().get(0).getId();
    }

    private String anyComicId() {
        return comicRepository.findAll().get(0).getId();
    }

    // ── Lectura: cualquier usuario autenticado ─────────────────────────────

    @Test
    @DisplayName("When USER gets collections, expect 200")
    @WithMockUser(roles = "USER")
    void when_user_gets_collections_expect_200() throws Exception {
        mockMvc.perform(get("/api/v1/collections")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("When anonymous gets collections, expect 401/403")
    void when_anonymous_gets_collections_expect_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/collections")).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("When USER gets user count, expect 200")
    @WithMockUser(roles = "USER")
    void when_user_gets_user_count_expect_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/count")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("When USER lists users, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_lists_users_expect_403() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When USER reads user details, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_reads_user_details_expect_403() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", "any-user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When USER creates a user, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_creates_user_expect_403() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"blocked\",\"mail\":\"blocked@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When ADMIN lists users, expect 200")
    @WithMockUser(roles = "ADMIN")
    void when_admin_lists_users_expect_200() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isOk());
    }

    // ── Toggles de usuario: cualquier usuario autenticado ──────────────────

    @Test
    @DisplayName("When USER toggles wantToComplete, expect 200")
    @WithMockUser(roles = "USER")
    void when_user_toggles_want_to_complete_expect_200() throws Exception {
        mockMvc.perform(patch("/api/v1/collections/{id}/want-to-complete", anyCollectionId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When USER toggles owned, expect 200")
    @WithMockUser(roles = "USER")
    void when_user_toggles_owned_expect_200() throws Exception {
        mockMvc.perform(patch("/api/v1/comics/{id}/owned", anyComicId()))
                .andExpect(status().isOk());
    }

    // ── Escritura de catálogo: solo ADMIN ──────────────────────────────────

    @Test
    @DisplayName("When USER creates a collection, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_creates_collection_expect_403() throws Exception {
        mockMvc.perform(post("/api/v1/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sec Test\",\"publisher\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When ADMIN creates a collection, expect 201")
    @WithMockUser(roles = "ADMIN")
    void when_admin_creates_collection_expect_201() throws Exception {
        mockMvc.perform(post("/api/v1/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sec Test Collection\",\"publisher\":\"Test\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("When USER deletes a comic, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_deletes_comic_expect_403() throws Exception {
        mockMvc.perform(delete("/api/v1/comics/{id}", anyComicId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When USER updates a collection, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_updates_collection_expect_403() throws Exception {
        mockMvc.perform(put("/api/v1/collections/{id}", anyCollectionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Whatever\",\"publisher\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When USER patches comic stock, expect 403")
    @WithMockUser(roles = "USER")
    void when_user_patches_stock_expect_403() throws Exception {
        mockMvc.perform(patch("/api/v1/comics/{id}/stock", anyComicId()).param("stock", "5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When ADMIN deletes an empty collection, expect 204")
    @WithMockUser(roles = "ADMIN")
    void when_admin_deletes_empty_collection_expect_204() throws Exception {
        Collection empty = collectionRepository.save(new Collection("Sec Empty", "Test", false, false));
        mockMvc.perform(delete("/api/v1/collections/{id}", empty.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When ADMIN deletes a collection with comics, expect 400")
    @WithMockUser(roles = "ADMIN")
    void when_admin_deletes_collection_with_comics_expect_400() throws Exception {
        Comic comic = comicRepository.findAll().get(0);
        Collection col = collectionRepository.findByName(comic.getCollectionName()).orElseThrow();
        mockMvc.perform(delete("/api/v1/collections/{id}", col.getId()))
                .andExpect(status().isBadRequest());
    }
}
