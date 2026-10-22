package com.comics.backend.repository;

import com.comics.backend.models.UserComic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserComicRepository extends MongoRepository<UserComic, String> {

    List<UserComic> findByUserId(String userId);

    Optional<UserComic> findByUserIdAndComicId(String userId, String comicId);

    boolean existsByUserIdAndComicId(String userId, String comicId);

    void deleteByUserIdAndComicId(String userId, String comicId);

    void deleteByUserId(String userId);

    void deleteByComicId(String comicId);
}
