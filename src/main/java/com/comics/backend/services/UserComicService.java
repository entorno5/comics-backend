package com.comics.backend.services;

import com.comics.backend.dto.UserComicPageResponse;
import com.comics.backend.dto.UserComicResponseDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.Comic;
import com.comics.backend.models.User;
import com.comics.backend.models.UserComic;
import com.comics.backend.repository.ComicRepository;
import com.comics.backend.repository.UserComicRepository;
import com.comics.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserComicService {

    private final UserComicRepository userComicRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    private User getUser(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("User", nickname));
    }

    public Optional<UserComicResponseDTO> getMyComic(String nickname, String comicId) {
        User user = getUser(nickname);
        return userComicRepository.findByUserIdAndComicId(user.getId(), comicId)
                .map(uc -> {
                    Comic comic = comicRepository.findById(comicId)
                            .orElseThrow(() -> new ResourceNotFoundException("Comic", comicId));
                    return UserComicResponseDTO.builder()
                            .id(uc.getId())
                            .comic(entityMapper.toComicResponseDTO(comic))
                            .units(uc.getUnits())
                            .addedAt(uc.getAddedAt())
                            .build();
                });
    }

    public UserComicPageResponse getMyComicsPage(String nickname, int page, int size, String q, boolean isMock) {
        User user = getUser(nickname);
        List<UserComic> userComics = userComicRepository.findByUserId(user.getId());

        if (userComics.isEmpty()) {
            return new UserComicPageResponse(Collections.emptyList(), 0, 0, isMock);
        }

        Set<String> comicIds = userComics.stream().map(UserComic::getComicId).collect(Collectors.toSet());
        Map<String, UserComic> ucByComicId = userComics.stream()
                .collect(Collectors.toMap(UserComic::getComicId, uc -> uc));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));
        Page<Comic> comicsPage = (q == null || q.isBlank())
                ? comicRepository.findByIdIn(comicIds, pageable)
                : comicRepository.findByIdInAndTitleContainsIgnoreCase(comicIds, q.trim(), pageable);

        List<UserComicResponseDTO> content = comicsPage.getContent().stream()
                .map(comic -> {
                    UserComic uc = ucByComicId.get(comic.getId());
                    return UserComicResponseDTO.builder()
                            .id(uc != null ? uc.getId() : null)
                            .comic(entityMapper.toComicResponseDTO(comic))
                            .units(uc != null ? uc.getUnits() : 1)
                            .addedAt(uc != null ? uc.getAddedAt() : null)
                            .build();
                })
                .collect(Collectors.toList());

        return new UserComicPageResponse(content, comicsPage.getTotalElements(), comicsPage.getTotalPages(), isMock);
    }

    public UserComicResponseDTO addMyComic(String nickname, String comicId, int units) {
        User user = getUser(nickname);

        if (userComicRepository.existsByUserIdAndComicId(user.getId(), comicId)) {
            throw new DuplicateResourceException("Comic " + comicId + " is already in your collection");
        }

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ResourceNotFoundException("Comic", comicId));

        UserComic uc = new UserComic();
        uc.setUserId(user.getId());
        uc.setComicId(comicId);
        uc.setUnits(Math.max(1, units));
        uc.setAddedAt(LocalDateTime.now());
        UserComic saved = userComicRepository.save(uc);

        log.info("User '{}' added comic '{}'", nickname, comicId);
        return UserComicResponseDTO.builder()
                .id(saved.getId())
                .comic(entityMapper.toComicResponseDTO(comic))
                .units(saved.getUnits())
                .addedAt(saved.getAddedAt())
                .build();
    }

    public UserComicResponseDTO updateMyComic(String nickname, String comicId, int units) {
        User user = getUser(nickname);

        UserComic uc = userComicRepository.findByUserIdAndComicId(user.getId(), comicId)
                .orElseThrow(() -> new ResourceNotFoundException("UserComic", comicId));

        uc.setUnits(Math.max(1, units));
        UserComic saved = userComicRepository.save(uc);

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ResourceNotFoundException("Comic", comicId));

        log.info("User '{}' updated comic '{}' units to {}", nickname, comicId, units);
        return UserComicResponseDTO.builder()
                .id(saved.getId())
                .comic(entityMapper.toComicResponseDTO(comic))
                .units(saved.getUnits())
                .addedAt(saved.getAddedAt())
                .build();
    }

    public void removeMyComic(String nickname, String comicId) {
        User user = getUser(nickname);

        if (!userComicRepository.existsByUserIdAndComicId(user.getId(), comicId)) {
            throw new ResourceNotFoundException("UserComic", comicId);
        }

        userComicRepository.deleteByUserIdAndComicId(user.getId(), comicId);
        log.info("User '{}' removed comic '{}'", nickname, comicId);
    }
}
