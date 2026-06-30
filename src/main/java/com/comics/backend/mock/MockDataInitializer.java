package com.comics.backend.mock;

import com.comics.backend.models.Comic;
import com.comics.backend.models.User;
import com.comics.backend.repository.ComicRepository;
import com.comics.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Loads sample data when the application starts in the "mock" profile.
 * Inserts a small catalog of comics and a demo user so the API returns
 * real-looking data without requiring a MongoDB instance.
 */
@Component
@Profile("mock")
@RequiredArgsConstructor
@Slf4j
public class MockDataInitializer implements ApplicationRunner {

    private final ComicRepository comicRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.debug("Loading mock seed data...");

        comicRepository.save(comic("The Amazing Spider-Man", 1, "Marvel",  3.99, "Peter Parker swings into action in his first solo adventure."));
        comicRepository.save(comic("The Amazing Spider-Man", 2, "Marvel",  3.99, "The Vulture threatens New York City."));
        comicRepository.save(comic("Batman",                 1, "DC",      3.99, "The Dark Knight faces a new enemy rising in Gotham."));
        comicRepository.save(comic("X-Men",                  1, "Marvel",  3.99, "Professor X assembles a team of mutants to protect humanity."));
        comicRepository.save(comic("Watchmen",               1, "DC",      4.99, "A complex story of retired superheroes in an alternate 1985."));

        User admin = new User();
        admin.setNickname("admin");
        admin.setName("Admin User");
        admin.setMail("admin@comics.local");
        admin.setPassword("$2a$12$mockHashedPassword");   // BCrypt placeholder — not usable for login
        admin.setRoles(Set.of("ADMIN", "USER"));
        admin.setActive(true);
        userRepository.save(admin);

        log.debug("Mock seed data loaded: {} comics, 1 user", comicRepository.count());
    }

    private Comic comic(String title, int number, String publisher, double price, String description) {
        Comic c = new Comic(title, number, publisher, price);
        c.setDescription(description);
        c.setStock(10);
        return c;
    }
}
