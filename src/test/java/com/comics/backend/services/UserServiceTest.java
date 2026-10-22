package com.comics.backend.services;

import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.repository.UserComicRepository;
import com.comics.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserComicRepository userComicRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityMapper entityMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userComicRepository, passwordEncoder, entityMapper);
    }

    @Test
    @DisplayName("When counting users, expect repository count returned")
    void when_counting_users_expect_repository_count_returned() {
        when(userRepository.count()).thenReturn(7L);

        long result = userService.countUsers();

        assertThat(result).isEqualTo(7L);
    }

    @Test
    @DisplayName("When deleting existing user, expect related comics deleted")
    void when_deleting_existing_user_expect_related_comics_deleted() {
        when(userRepository.existsById("user-1")).thenReturn(true);

        userService.deleteUser("user-1");

        verify(userComicRepository).deleteByUserId("user-1");
        verify(userRepository).deleteById("user-1");
    }
}
