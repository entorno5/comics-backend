package com.comics.backend.mock;

import com.comics.backend.models.Collection;
import com.comics.backend.models.Comic;
import com.comics.backend.models.User;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.ComicRepository;
import com.comics.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.debug("Loading mock seed data...");

        // â”€â”€ Collections â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        collectionRepository.save(new Collection("The Amazing Spider-Man", "Marvel", true,  true));
        collectionRepository.save(new Collection("X-Men",                  "Marvel", true,  true));
        collectionRepository.save(new Collection("Uncanny X-Men",          "Marvel", true,  false));
        collectionRepository.save(new Collection("The Avengers",           "Marvel", true,  true));
        collectionRepository.save(new Collection("Iron Man",               "Marvel", true,  false));
        collectionRepository.save(new Collection("Thor",                   "Marvel", true,  true));
        collectionRepository.save(new Collection("Daredevil",              "Marvel", true,  false));
        collectionRepository.save(new Collection("Black Panther",          "Marvel", true,  false));
        collectionRepository.save(new Collection("Captain America",        "Marvel", true,  true));
        collectionRepository.save(new Collection("Hulk",                   "Marvel", true,  false));
        collectionRepository.save(new Collection("Silver Surfer",          "Marvel", false, true));
        collectionRepository.save(new Collection("Batman",                 "DC",     true,  true));
        collectionRepository.save(new Collection("Superman",               "DC",     true,  false));
        collectionRepository.save(new Collection("Wonder Woman",           "DC",     true,  true));
        collectionRepository.save(new Collection("Justice League",         "DC",     true,  false));
        collectionRepository.save(new Collection("Watchmen",               "DC",     false, true));
        collectionRepository.save(new Collection("Saga",                   "Image",  true,  true));
        collectionRepository.save(new Collection("Spawn",                  "Image",  true,  false));
        collectionRepository.save(new Collection("Invincible",             "Image",  false, true));
        collectionRepository.save(new Collection("Hellboy",                "Dark Horse", false, true));

        // â”€â”€ Comics (title = collectionName, ordered by publishedDate inside each series) â”€â”€

        // Marvel â€” The Amazing Spider-Man
        comicRepository.save(comic("The Amazing Spider-Man", 1, "Marvel", 3.99, "Peter Parker swings into action in his first solo adventure.", LocalDate.of(2020, 1, 15)));
        comicRepository.save(comic("The Amazing Spider-Man", 2, "Marvel", 3.99, "The Vulture threatens New York City.",                          LocalDate.of(2020, 3, 1)));
        comicRepository.save(comic("The Amazing Spider-Man", 3, "Marvel", 3.99, "Spider-Man faces the Sandman in a brutal encounter.",           LocalDate.of(2020, 5, 15)));
        comicRepository.save(comic("The Amazing Spider-Man", 4, "Marvel", 3.99, "Electro makes his debut, and the city is powerless.",           LocalDate.of(2020, 7, 1)));
        comicRepository.save(comic("The Amazing Spider-Man", 5, "Marvel", 3.99, "Doctor Octopus arrives to challenge our hero.",                 LocalDate.of(2020, 9, 15)));

        // Marvel â€” X-Men
        comicRepository.save(comic("X-Men", 1, "Marvel", 3.99, "Professor X assembles a team of mutants to protect humanity.", LocalDate.of(2019, 6, 1)));
        comicRepository.save(comic("X-Men", 2, "Marvel", 3.99, "Magneto returns with a new plan to dominate mankind.",         LocalDate.of(2019, 8, 1)));
        comicRepository.save(comic("X-Men", 3, "Marvel", 3.99, "The Blob proves to be an unstoppable foe.",                    LocalDate.of(2019, 10, 1)));

        // Marvel â€” Uncanny X-Men
        comicRepository.save(comic("Uncanny X-Men", 1, "Marvel", 4.49, "A new era begins for the strangest heroes of all.",  LocalDate.of(2021, 2, 1)));
        comicRepository.save(comic("Uncanny X-Men", 2, "Marvel", 4.49, "Dark Phoenix rises and threatens the universe.",      LocalDate.of(2021, 4, 1)));

        // Marvel â€” The Avengers
        comicRepository.save(comic("The Avengers", 1, "Marvel", 3.99, "Earth's mightiest heroes unite for the first time.",    LocalDate.of(2022, 1, 1)));
        comicRepository.save(comic("The Avengers", 2, "Marvel", 3.99, "The Space Phantom sows discord among the Avengers.",    LocalDate.of(2022, 3, 1)));
        comicRepository.save(comic("The Avengers", 3, "Marvel", 3.99, "The Hulk leaves the team in dramatic fashion.",         LocalDate.of(2022, 5, 1)));
        comicRepository.save(comic("The Avengers", 4, "Marvel", 3.99, "Captain America is revived from the ice.",              LocalDate.of(2022, 7, 1)));

        // Marvel â€” Iron Man
        comicRepository.save(comic("Iron Man", 1, "Marvel", 3.99, "Tony Stark takes flight for the first time in the Iron Man suit.", LocalDate.of(2021, 9, 1)));
        comicRepository.save(comic("Iron Man", 2, "Marvel", 3.99, "The Mandarin strikes at Stark Industries.",                        LocalDate.of(2021, 11, 1)));

        // Marvel â€” Thor
        comicRepository.save(comic("Thor", 1, "Marvel", 3.99, "The God of Thunder descends to protect the Earth.", LocalDate.of(2023, 1, 1)));
        comicRepository.save(comic("Thor", 2, "Marvel", 3.99, "Loki schemes to seize the throne of Asgard.",       LocalDate.of(2023, 3, 1)));

        // Marvel â€” Daredevil
        comicRepository.save(comic("Daredevil", 1, "Marvel", 3.99, "Matt Murdock fights crime as the Man Without Fear.",   LocalDate.of(2020, 11, 1)));
        comicRepository.save(comic("Daredevil", 2, "Marvel", 3.99, "Electro and the Eel team up against the blind hero.",  LocalDate.of(2021, 1, 1)));

        // Marvel â€” Black Panther
        comicRepository.save(comic("Black Panther", 1, "Marvel", 3.99, "T'Challa defends Wakanda from an ancient threat.", LocalDate.of(2023, 5, 1)));

        // Marvel â€” Captain America
        comicRepository.save(comic("Captain America", 1, "Marvel", 3.99, "Steve Rogers fights for freedom against HYDRA.", LocalDate.of(2024, 2, 1)));

        // Marvel â€” Hulk
        comicRepository.save(comic("Hulk", 1, "Marvel", 3.99, "Bruce Banner struggles to control the raging monster within.", LocalDate.of(2022, 9, 1)));
        comicRepository.save(comic("Hulk", 2, "Marvel", 3.99, "The Leader plots to use gamma radiation against humanity.",    LocalDate.of(2022, 11, 1)));

        // Marvel â€” Silver Surfer
        comicRepository.save(comic("Silver Surfer", 1, "Marvel", 3.99, "Norrin Radd soars the cosmos as herald of Galactus.", LocalDate.of(2019, 3, 1)));

        // DC â€” Batman
        comicRepository.save(comic("Batman", 1, "DC", 3.99, "The Dark Knight faces a new enemy rising in Gotham.",       LocalDate.of(2019, 1, 1)));
        comicRepository.save(comic("Batman", 2, "DC", 3.99, "The Joker escapes Arkham Asylum with a terrifying plan.",   LocalDate.of(2019, 4, 1)));
        comicRepository.save(comic("Batman", 3, "DC", 3.99, "Ra's al Ghul tests the limits of Bruce Wayne's code.",      LocalDate.of(2019, 7, 1)));
        comicRepository.save(comic("Batman", 4, "DC", 3.99, "Bane breaks Batman in the most brutal confrontation yet.",  LocalDate.of(2019, 10, 1)));

        // DC â€” Superman
        comicRepository.save(comic("Superman", 1, "DC", 3.99, "The Man of Steel arrives in Metropolis for the first time.", LocalDate.of(2020, 2, 1)));
        comicRepository.save(comic("Superman", 2, "DC", 3.99, "Lex Luthor unveils a deadly kryptonite weapon.",              LocalDate.of(2020, 5, 1)));
        comicRepository.save(comic("Superman", 3, "DC", 3.99, "Brainiac threatens to shrink Metropolis.",                    LocalDate.of(2020, 8, 1)));

        // DC â€” Wonder Woman
        comicRepository.save(comic("Wonder Woman", 1, "DC", 3.99, "Diana of Themyscira arrives in Man's World.",             LocalDate.of(2021, 6, 1)));
        comicRepository.save(comic("Wonder Woman", 2, "DC", 3.99, "Ares plots a new war and only Wonder Woman can stop it.", LocalDate.of(2021, 8, 1)));

        // DC â€” Justice League
        comicRepository.save(comic("Justice League", 1, "DC", 4.49, "Earth's greatest heroes form an alliance against Starro.", LocalDate.of(2023, 7, 1)));
        comicRepository.save(comic("Justice League", 2, "DC", 4.49, "Darkseid makes his move against the league.",              LocalDate.of(2023, 9, 1)));

        // DC â€” Watchmen (colecciÃ³n cerrada)
        comicRepository.save(comic("Watchmen", 1, "DC", 4.99, "A complex story of retired superheroes in an alternate 1985.", LocalDate.of(2018, 3, 1)));
        comicRepository.save(comic("Watchmen", 2, "DC", 4.99, "Rorschach investigates the murder of the Comedian.",            LocalDate.of(2018, 5, 1)));
        comicRepository.save(comic("Watchmen", 3, "DC", 4.99, "Dr. Manhattan begins to detach from humanity.",                 LocalDate.of(2018, 7, 1)));

        // Image Comics â€” Saga
        comicRepository.save(comic("Saga", 1, "Image", 3.99, "An epic space opera following two lovers from warring races.", LocalDate.of(2024, 3, 1)));
        comicRepository.save(comic("Saga", 2, "Image", 3.99, "The Will and Lying Cat hunt down the fugitive family.",        LocalDate.of(2024, 5, 1)));

        // Image Comics â€” Spawn
        comicRepository.save(comic("Spawn", 1, "Image", 3.99, "Al Simmons returns from the dead as a hellspawn warrior.", LocalDate.of(2024, 1, 1)));

        // Image Comics â€” Invincible
        comicRepository.save(comic("Invincible", 1, "Image", 3.99, "Mark Grayson discovers his father is the world's greatest hero.", LocalDate.of(2017, 6, 1)));

        // Dark Horse â€” Hellboy
        comicRepository.save(comic("Hellboy", 1, "Dark Horse", 3.99, "Hellboy investigates a supernatural conspiracy.",  LocalDate.of(2016, 4, 1)));
        comicRepository.save(comic("Hellboy", 2, "Dark Horse", 3.99, "The seed of destruction puts the world at risk.",  LocalDate.of(2016, 8, 1)));

        // â”€â”€ Users â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        User admin = new User();
        admin.setNickname("admin");
        admin.setName("Admin User");
        admin.setMail("admin@comics.local");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setRoles(Set.of("ADMIN", "USER"));
        admin.setActive(true);
        userRepository.save(admin);

        User reader = new User();
        reader.setNickname("reader");
        reader.setName("Comic Reader");
        reader.setMail("reader@comics.local");
        reader.setPassword(passwordEncoder.encode("Reader123!"));
        reader.setRoles(Set.of("USER"));
        reader.setActive(true);
        userRepository.save(reader);

        log.debug("Mock seed data loaded: {} comics, {} collections, 2 users",
                comicRepository.count(), collectionRepository.count());
    }

    private Comic comic(String title, int number, String publisher, double price,
                        String description, LocalDate publishedDate) {
        Comic c = new Comic(title, number, publisher, price);
        c.setDescription(description);
        c.setStock(10);
        c.setPublishedDate(publishedDate);
        return c;
    }
}
