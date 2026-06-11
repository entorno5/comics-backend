package com.comics.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.comics.backend.models.Comic;
import java.util.List;
import java.util.Optional;

public interface ComicRepository extends MongoRepository<Comic, String> {
    Optional<Comic> findByTitleAndNumber(String title, int number);
    
    List<Comic> findByTitle(String title);
    
    // Case-insensitive search - finds comics with titles containing the search term
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Comic> findByTitleContainsIgnoreCase(String titleSearch);
}
