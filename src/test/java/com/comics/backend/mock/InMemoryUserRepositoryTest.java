package com.comics.backend.mock;

import com.comics.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InMemoryUserRepository Tests")
class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    // ── save / findById ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Save and find by ID")
    class SaveAndFindById {

        @Test
        @DisplayName("When saving a new user without ID, expect ID generated and user persisted")
        void when_saving_new_user_without_id_expect_id_generated() {
            User user = user("peter", "peter@marvel.com");

            User saved = repository.save(user);

            assertThat(saved.getId()).isNotNull().startsWith("mock-user-");
            assertThat(repository.findById(saved.getId())).isPresent();
        }

        @Test
        @DisplayName("When saving a user with existing ID, expect user updated")
        void when_saving_user_with_existing_id_expect_updated() {
            User u = user("peter", "peter@marvel.com");
            u.setId("fixed-id");
            repository.save(u);

            u.setName("Peter Parker");
            repository.save(u);

            assertThat(repository.count()).isEqualTo(1);
            assertThat(repository.findById("fixed-id").get().getName()).isEqualTo("Peter Parker");
        }

        @Test
        @DisplayName("When finding by non-existent ID, expect empty Optional")
        void when_finding_by_nonexistent_id_expect_empty() {
            assertThat(repository.findById("ghost")).isEmpty();
        }
    }

    // ── existsById / count ────────────────────────────────────────────────────

    @Nested
    @DisplayName("existsById and count")
    class ExistsAndCount {

        @Test
        @DisplayName("When user saved, expect existsById true and count 1")
        void when_user_saved_expect_exists_and_count() {
            User saved = repository.save(user("bruce", "bruce@dc.com"));

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
        @DisplayName("When multiple users saved, expect all returned")
        void when_multiple_users_saved_expect_all_returned() {
            repository.save(user("peter", "peter@marvel.com"));
            repository.save(user("bruce", "bruce@dc.com"));

            assertThat(repository.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("When pageable requested, expect correct page content and total")
        void when_pageable_requested_expect_correct_page() {
            repository.save(user("a", "a@test.com"));
            repository.save(user("b", "b@test.com"));
            repository.save(user("c", "c@test.com"));

            Page<User> page = repository.findAll(PageRequest.of(0, 2));

            assertThat(page.getTotalElements()).isEqualTo(3);
            assertThat(page.getContent()).hasSize(2);
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Delete")
    class Delete {

        @Test
        @DisplayName("When deleting by ID, expect user removed")
        void when_deleting_by_id_expect_removed() {
            User saved = repository.save(user("peter", "peter@marvel.com"));
            repository.deleteById(saved.getId());

            assertThat(repository.existsById(saved.getId())).isFalse();
            assertThat(repository.count()).isEqualTo(0);
        }

        @Test
        @DisplayName("When deleteAll called, expect store empty")
        void when_delete_all_expect_empty() {
            repository.save(user("a", "a@test.com"));
            repository.save(user("b", "b@test.com"));
            repository.deleteAll();

            assertThat(repository.count()).isEqualTo(0);
        }
    }

    // ── Custom query methods ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Custom query methods")
    class CustomQueryMethods {

        @Test
        @DisplayName("When searching by existing nickname, expect user returned")
        void when_search_by_existing_nickname_expect_match() {
            repository.save(user("spidey", "spidey@marvel.com"));

            Optional<User> result = repository.findByNickname("spidey");

            assertThat(result).isPresent();
            assertThat(result.get().getNickname()).isEqualTo("spidey");
        }

        @Test
        @DisplayName("When searching by non-existent nickname, expect empty")
        void when_search_by_nonexistent_nickname_expect_empty() {
            assertThat(repository.findByNickname("ghost")).isEmpty();
        }

        @Test
        @DisplayName("When searching by existing mail, expect user returned")
        void when_search_by_existing_mail_expect_match() {
            repository.save(user("clark", "clark@dc.com"));

            Optional<User> result = repository.findByMail("clark@dc.com");

            assertThat(result).isPresent();
            assertThat(result.get().getMail()).isEqualTo("clark@dc.com");
        }

        @Test
        @DisplayName("When searching by non-existent mail, expect empty")
        void when_search_by_nonexistent_mail_expect_empty() {
            assertThat(repository.findByMail("nobody@unknown.com")).isEmpty();
        }
    }

    // ── Batch operations ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Batch operations")
    class BatchOperations {

        @Test
        @DisplayName("When saveAll called with list, expect all persisted")
        void when_save_all_expect_all_persisted() {
            List<User> saved = repository.saveAll(List.of(
                    user("a", "a@test.com"),
                    user("b", "b@test.com")
            ));

            assertThat(saved).hasSize(2);
            assertThat(repository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("When findAllById called, expect only matching users returned")
        void when_find_all_by_id_expect_matching_only() {
            User a = repository.save(user("a", "a@test.com"));
            repository.save(user("b", "b@test.com"));

            List<User> found = repository.findAllById(List.of(a.getId()));

            assertThat(found).hasSize(1);
            assertThat(found.get(0).getNickname()).isEqualTo("a");
        }
    }

    // ── Unsupported operations ────────────────────────────────────────────────

    @Nested
    @DisplayName("Unsupported QueryByExample operations")
    class UnsupportedOperations {

        @Test
        @DisplayName("When findOne by Example called, expect UnsupportedOperationException")
        void when_find_one_by_example_expect_unsupported() {
            assertThatThrownBy(() -> repository.findOne(org.springframework.data.domain.Example.of(new User())))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User user(String nickname, String mail) {
        User u = new User();
        u.setNickname(nickname);
        u.setName(nickname + " User");
        u.setMail(mail);
        u.setPassword("hashed");
        u.setActive(true);
        return u;
    }
}
