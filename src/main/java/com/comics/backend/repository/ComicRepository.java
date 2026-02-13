package com.comics.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.comics.backend.models.Comic;
import java.util.Optional;

public interface ComicRepository extends MongoRepository<Comic, String> {
    Optional<Comic> findByTitle(String title);
}
