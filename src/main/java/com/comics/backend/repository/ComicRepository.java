package com.comics.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.comics.backend.models.Comic;
import java.util.List;
import java.util.Optional;

public interface ComicRepository extends MongoRepository<Comic, String> {
    Optional<Comic> findByTitleAndNumber(String title, int number);
    
    List<Comic> findByTitle(String title);

    List<Comic> findByCollectionName(String collectionName);

    long countByCollectionName(String collectionName);

    long countByCollectionNameAndOwnedTrue(String collectionName);

    long countByOwnedTrue();

    Page<Comic> findByOwnedTrue(Pageable pageable);

    // Case-insensitive search - finds comics with titles containing the search term
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Comic> findByTitleContainsIgnoreCase(String titleSearch);

    // Paginada — para el filtro de búsqueda global
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Page<Comic> findByTitleContainsIgnoreCasePaged(String titleSearch, Pageable pageable);

    // Paginada, solo ejemplares que tengo — búsqueda dentro de "mis cómics"
    @Query("{ 'title': { $regex: ?0, $options: 'i' }, 'owned': true }")
    Page<Comic> findByTitleContainsIgnoreCaseAndOwnedTruePaged(String titleSearch, Pageable pageable);

    // Por-usuario: comics de un conjunto de IDs (para Mis cómics)
    @Query("{ '_id': { $in: ?0 } }")
    Page<Comic> findByIdIn(java.util.Collection<String> ids, Pageable pageable);

    @Query("{ '_id': { $in: ?0 }, 'title': { $regex: ?1, $options: 'i' } }")
    Page<Comic> findByIdInAndTitleContainsIgnoreCase(java.util.Collection<String> ids, String titleSearch, Pageable pageable);
}
