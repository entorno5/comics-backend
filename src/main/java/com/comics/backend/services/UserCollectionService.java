package com.comics.backend.services;

import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.User;
import com.comics.backend.models.UserComic;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.ComicRepository;
import com.comics.backend.repository.UserComicRepository;
import com.comics.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCollectionService {

    private final UserComicRepository userComicRepository;
    private final CollectionRepository collectionRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    private User getUser(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("User", nickname));
    }

    /** Returns all collections where the user owns at least one comic. */
    public List<CollectionResponseDTO> getMyCollections(String nickname) {
        User user = getUser(nickname);
        List<UserComic> userComics = userComicRepository.findByUserId(user.getId());
        if (userComics.isEmpty()) return Collections.emptyList();

        Set<String> comicIds = userComics.stream()
                .map(UserComic::getComicId)
                .collect(Collectors.toSet());

        Set<String> colNames = comicRepository.findAllById(comicIds).stream()
                .map(com.comics.backend.models.Comic::getCollectionName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.toSet());

        return colNames.stream()
                .map(name -> collectionRepository.findByName(name).orElse(null))
                .filter(Objects::nonNull)
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(col -> {
                    long issueCount = comicRepository.countByCollectionName(col.getName());
                    return entityMapper.toCollectionResponseDTO(col, issueCount, 0);
                })
                .collect(Collectors.toList());
    }
}
