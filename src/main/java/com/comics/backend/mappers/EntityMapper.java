package com.comics.backend.mappers;

import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateComicDTO;
import com.comics.backend.dto.CreateUserDTO;
import com.comics.backend.dto.UserResponseDTO;
import com.comics.backend.models.Collection;
import com.comics.backend.models.Comic;
import com.comics.backend.models.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between DTOs and entities.
 * Handles transformation logic between data transfer objects and domain models.
 */
@Component
public class EntityMapper {

    /**
     * Convert CreateUserDTO to User entity
     */
    public User toUserEntity(CreateUserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setNickname(dto.getNickname());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setMail(dto.getMail());
        return user;
    }

    /**
     * Convert User entity to UserResponseDTO
     */
    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponseDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .name(user.getName())
                .mail(user.getMail())
                .roles(user.getRoles())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert CreateComicDTO to Comic entity
     */
    public Comic toComicEntity(CreateComicDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Comic comic = new Comic();
        comic.setTitle(dto.getTitle());
        comic.setCollectionName(dto.getCollectionName() != null ? dto.getCollectionName() : dto.getTitle());
        comic.setNumber(dto.getNumber());
        comic.setPublisher(dto.getPublisher());
        comic.setPrice(dto.getPrice());
        comic.setDescription(dto.getDescription());
        comic.setStock(dto.getStock());
        comic.setPublishedDate(dto.getPublishedDate());
        return comic;
    }

    /**
     * Convert Comic entity to ComicResponseDTO
     */
    public ComicResponseDTO toComicResponseDTO(Comic comic) {
        if (comic == null) {
            return null;
        }
        
        return ComicResponseDTO.builder()
                .id(comic.getId())
                .title(comic.getTitle())
                .collectionName(comic.getCollectionName())
                .number(comic.getNumber())
                .publisher(comic.getPublisher())
                .price(comic.getPrice())
                .description(comic.getDescription())
                .stock(comic.getStock())
                .active(comic.getActive())
                .publishedDate(comic.getPublishedDate())
                .createdAt(comic.getCreatedAt())
                .updatedAt(comic.getUpdatedAt())
                .build();
    }

    /**
     * Convert Collection entity to CollectionResponseDTO with computed issue count.
     */
    public CollectionResponseDTO toCollectionResponseDTO(Collection collection, long issueCount) {
        if (collection == null) {
            return null;
        }
        
        return CollectionResponseDTO.builder()
                .id(collection.getId())
                .name(collection.getName())
                .publisher(collection.getPublisher())
                .ongoingCollection(collection.getOngoingCollection())
                .wantToComplete(collection.getWantToComplete())
                .issueCount(issueCount)
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }
}