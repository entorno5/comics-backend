package com.comics.backend.repository;

import com.comics.backend.models.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CollectionRepository extends MongoRepository<Collection, String> {

    Optional<Collection> findByName(String name);

    boolean existsByName(String name);
}
