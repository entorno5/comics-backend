package com.comics.backend.mock;

import com.comics.backend.models.Comic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InMemoryComicRepository Tests")
class InMemoryComicRepositoryTest {

    private InMemoryComicRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryComicRepository();
    }

    // ── save / findById ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Save and find by ID")
    class SaveAndFindById {

        @Test
        @DisplayName("When saving a new comic without ID, expect ID generated and comic persisted")
        void when_saving_new_comic_without_id_expect_id_generated() {
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);

            Comic saved = repository.save(comic);

            assertThat(saved.getId()).isNotNull().startsWith("mock-comic-");
            assertThat(repository.findById(saved.getId())).isPresent();
        }

        @Test
        @DisplayName("When saving a comic with existing ID, expect comic updated")
        void when_saving_comic_with_existing_id_expect_comic_updated() {
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            comic.setId("fixed-id");
            repository.save(comic);

            comic.setPublisher("Marvel Comics");
            repository.save(comic);

            assertThat(repository.count()).isEqualTo(1);
            assertThat(repository.findById("fixed-id").get().getPublisher()).isEqualTo("Marvel Comics");
        }

        @Test
        @DisplayName("When finding by non-existent ID, expect empty Optional")
        void when_finding_by_nonexistent_id_expect_empty() {
            assertThat(repository.findById("does-not-exist")).isEmpty();
        }
    }

    // ── existsById / count ────────────────────────────────────────────────────

    @Nested
    @DisplayName("existsById and count")
    class ExistsAndCount {

        @Test
        @DisplayName("When comic saved, expect existsById true and count 1")
        void when_comic_saved_expect_exists_and_count() {
            Comic saved = repository.save(new Comic("Batman", 1, "DC", 3.99));

            assertThat(repository.existsById(saved.getId())).isTrue();
            assertThat(repository.existsById("ghost")).isFalse();
            assertThat(repository.count()).isEqualTo(1);
        }
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("When multiple comics saved, expect all returned")
        void when_multiple_comics_saved_expect_all_returned() {
            repository.save(new Comic("Spider-Man", 1, "Marvel", 3.99));
            repository.save(new Comic("Batman",     1, "DC",     3.99));

            assertThat(repository.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("When pageable requested, expect correct page content and total")
        void when_pageable_requested_expect_correct_page() {
            repository.save(new Comic("A", 1, "Marvel", 1.0));
            repository.save(new Comic("B", 1, "Marvel", 1.0));
            repository.save(new Comic("C", 1, "Marvel", 1.0));

            Page<Comic> page = repository.findAll(PageRequest.of(0, 2));

            assertThat(page.getTotalElements()).isEqualTo(3);
            assertThat(page.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("When requesting page beyond content, expect empty content")
        void when_page_beyond_content_expect_empty() {
            repository.save(new Comic("A", 1, "Marvel", 1.0));

            Page<Comic> page = repository.findAll(PageRequest.of(5, 10));

            assertThat(page.getContent()).isEmpty();
            assertThat(page.getTotalElements()).isEqualTo(1);
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Delete")
    class Delete {

        @Test
        @DisplayName("When deleting by ID, expect comic removed")
        void when_deleting_by_id_expect_removed() {
            Comic saved = repository.save(new Comic("X-Men", 1, "Marvel", 3.99));
            repository.deleteById(saved.getId());

            assertThat(repository.existsById(saved.getId())).isFalse();
            assertThat(repository.count()).isEqualTo(0);
        }

        @Test
        @DisplayName("When deleteAll called, expect store empty")
        void when_delete_all_expect_empty() {
            repository.save(new Comic("A", 1, "Marvel", 1.0));
            repository.save(new Comic("B", 1, "Marvel", 1.0));
            repository.deleteAll();

            assertThat(repository.count()).isEqualTo(0);
        }
    }

    // ── Custom query methods ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Custom query methods")
    class CustomQueryMethods {

        @Test
        @DisplayName("When searching by exact title and number, expect match returned")
        void when_search_by_title_and_number_expect_match() {
            repository.save(new Comic("Spider-Man", 1, "Marvel", 3.99));
            repository.save(new Comic("Spider-Man", 2, "Marvel", 3.99));

            Optional<Comic> result = repository.findByTitleAndNumber("Spider-Man", 2);

            assertThat(result).isPresent();
            assertThat(result.get().getNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("When searching by non-existent title and number, expect empty")
        void when_search_by_nonexistent_title_and_number_expect_empty() {
            assertThat(repository.findByTitleAndNumber("Ghost", 99)).isEmpty();
        }

        @Test
        @DisplayName("When searching by title, expect all matching comics returned")
        void when_search_by_title_expect_all_matching() {
            repository.save(new Comic("Spider-Man", 1, "Marvel", 3.99));
            repository.save(new Comic("Spider-Man", 2, "Marvel", 3.99));
            repository.save(new Comic("Batman",     1, "DC",     3.99));

            List<Comic> results = repository.findByTitle("Spider-Man");

            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("When searching case-insensitive partial match, expect all matching returned")
        void when_search_by_title_contains_ignore_case_expect_matches() {
            repository.save(new Comic("The Amazing Spider-Man", 1, "Marvel", 3.99));
            repository.save(new Comic("Spider-Man Noir",        1, "Marvel", 3.99));
            repository.save(new Comic("Batman",                 1, "DC",     3.99));

            List<Comic> results = repository.findByTitleContainsIgnoreCase("spider");

            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("When case-insensitive search matches nothing, expect empty list")
        void when_case_insensitive_search_no_match_expect_empty() {
            repository.save(new Comic("Batman", 1, "DC", 3.99));

            assertThat(repository.findByTitleContainsIgnoreCase("hulk")).isEmpty();
        }
    }

    // ── saveAll / findAllById ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Batch operations")
    class BatchOperations {

        @Test
        @DisplayName("When saveAll called with list, expect all persisted")
        void when_save_all_expect_all_persisted() {
            List<Comic> saved = repository.saveAll(List.of(
                    new Comic("A", 1, "Marvel", 1.0),
                    new Comic("B", 1, "Marvel", 1.0)
            ));

            assertThat(saved).hasSize(2);
            assertThat(repository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("When findAllById called, expect only matching comics returned")
        void when_find_all_by_id_expect_matching_only() {
            Comic a = repository.save(new Comic("A", 1, "Marvel", 1.0));
            repository.save(new Comic("B", 1, "Marvel", 1.0));

            List<Comic> found = repository.findAllById(List.of(a.getId()));

            assertThat(found).hasSize(1);
            assertThat(found.get(0).getTitle()).isEqualTo("A");
        }
    }

    // ── Unsupported operations ────────────────────────────────────────────────

    @Nested
    @DisplayName("Unsupported QueryByExample operations")
    class UnsupportedOperations {

        @Test
        @DisplayName("When findOne by Example called, expect UnsupportedOperationException")
        void when_find_one_by_example_expect_unsupported() {
            assertThatThrownBy(() -> repository.findOne(Example.of(new Comic())))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
