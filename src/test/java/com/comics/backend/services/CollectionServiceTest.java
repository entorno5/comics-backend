package com.comics.backend.services;

import com.comics.backend.dto.CollectionResponseDTO;
import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateCollectionDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.exceptions.ValidationException;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.models.Collection;
import com.comics.backend.models.Comic;
import com.comics.backend.repository.CollectionRepository;
import com.comics.backend.repository.ComicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CollectionService Tests")
class CollectionServiceTest {

    private CollectionService collectionService;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private ComicRepository comicRepository;

    @Mock
    private EntityMapper entityMapper;

    @BeforeEach
    void beforeEach() {
        collectionService = new CollectionService(collectionRepository, comicRepository, entityMapper);
    }

    @Nested
    @DisplayName("Get Collections Page Tests")
    class GetCollectionsPageTests {

        private Collection col(String name) {
            Collection c = new Collection(name, "Test", true, false);
            c.setId("id-" + name);
            return c;
        }

        @Test
        @DisplayName("When 3 collections and size 2, expect first page sorted with 2 items")
        void when_three_collections_size_two_expect_first_page_sorted() {
            when(collectionRepository.findAll()).thenReturn(List.of(col("Saga"), col("Batman"), col("X-Men")));
            when(comicRepository.countByCollectionName(any())).thenReturn(0L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenAnswer(inv -> {
                        Collection c = inv.getArgument(0);
                        return CollectionResponseDTO.builder().name(c.getName()).build();
                    });

            var page0 = collectionService.getCollectionsPage(0, 2, false);

            assertThat(page0.totalElements()).isEqualTo(3);
            assertThat(page0.totalPages()).isEqualTo(2);
            assertThat(page0.content()).extracting(CollectionResponseDTO::getName)
                    .containsExactly("Batman", "Saga");
        }

        @Test
        @DisplayName("When requesting last page, expect remaining items")
        void when_last_page_expect_remaining_items() {
            when(collectionRepository.findAll()).thenReturn(List.of(col("Saga"), col("Batman"), col("X-Men")));
            when(comicRepository.countByCollectionName(any())).thenReturn(0L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenAnswer(inv -> {
                        Collection c = inv.getArgument(0);
                        return CollectionResponseDTO.builder().name(c.getName()).build();
                    });

            var page1 = collectionService.getCollectionsPage(1, 2, false);

            assertThat(page1.content()).extracting(CollectionResponseDTO::getName)
                    .containsExactly("X-Men");
        }

        @Test
        @DisplayName("When page is out of range, expect empty content")
        void when_page_out_of_range_expect_empty_content() {
            when(collectionRepository.findAll()).thenReturn(List.of(col("Batman")));

            var page = collectionService.getCollectionsPage(5, 2, false);

            assertThat(page.content()).isEmpty();
            assertThat(page.totalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("When ownedOnly, expect only collections with owned issues")
        void when_owned_only_expect_only_collections_with_owned_issues() {
            when(collectionRepository.findAll()).thenReturn(List.of(col("Saga"), col("Batman"), col("X-Men")));
            when(comicRepository.countByCollectionNameAndOwnedTrue("Batman")).thenReturn(2L);
            when(comicRepository.countByCollectionNameAndOwnedTrue("Saga")).thenReturn(0L);
            when(comicRepository.countByCollectionNameAndOwnedTrue("X-Men")).thenReturn(1L);
            when(comicRepository.countByCollectionName(any())).thenReturn(3L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenAnswer(inv -> {
                        Collection c = inv.getArgument(0);
                        return CollectionResponseDTO.builder()
                                .name(c.getName())
                                .ownedCount(inv.getArgument(2))
                                .build();
                    });

            var page = collectionService.getCollectionsPage(0, 10, true);

            assertThat(page.totalElements()).isEqualTo(2);
            assertThat(page.content()).extracting(CollectionResponseDTO::getName)
                    .containsExactly("Batman", "X-Men");
            assertThat(page.content()).extracting(CollectionResponseDTO::getOwnedCount)
                    .containsExactly(2L, 1L);
        }
    }

    @Nested
    @DisplayName("Get Collection Tests")
    class GetCollectionTests {

        @Test
        @DisplayName("When collection exists, expect DTO with issue count")
        void when_collection_exists_expect_dto_with_issue_count() {
            Collection col = new Collection("Batman", "DC", true, true);
            col.setId("col-1");
            CollectionResponseDTO dto = new CollectionResponseDTO();

            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(comicRepository.countByCollectionName("Batman")).thenReturn(4L);
            when(comicRepository.countByCollectionNameAndOwnedTrue("Batman")).thenReturn(2L);
            when(entityMapper.toCollectionResponseDTO(col, 4L, 2L)).thenReturn(dto);

            CollectionResponseDTO result = collectionService.getCollection("col-1");

            assertThat(result).isSameAs(dto);
            verify(entityMapper).toCollectionResponseDTO(col, 4L, 2L);
        }

        @Test
        @DisplayName("When collection not found, expect ResourceNotFoundException")
        void when_collection_not_found_expect_resource_not_found_exception() {
            when(collectionRepository.findById("missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> collectionService.getCollection("missing"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("missing");
        }
    }

    @Nested
    @DisplayName("Get Collection Comics Tests")
    class GetCollectionComicsTests {

        @Test
        @DisplayName("When comics exist, expect them ordered by number ascending")
        void when_comics_exist_expect_ordered_by_number_ascending() {
            Collection col = new Collection("Batman", "DC", true, true);
            col.setId("col-1");
            Comic issue3 = new Comic("Batman", 3, "DC", 3.99);
            Comic issue1 = new Comic("Batman", 1, "DC", 3.99);
            Comic issue2 = new Comic("Batman", 2, "DC", 3.99);

            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(comicRepository.findByCollectionName("Batman")).thenReturn(List.of(issue3, issue1, issue2));
            when(entityMapper.toComicResponseDTO(any(Comic.class))).thenAnswer(inv -> {
                Comic c = inv.getArgument(0);
                ComicResponseDTO dto = new ComicResponseDTO();
                dto.setNumber(c.getNumber());
                return dto;
            });

            List<ComicResponseDTO> result = collectionService.getCollectionComics("col-1");

            assertThat(result).extracting(ComicResponseDTO::getNumber).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("When collection not found, expect ResourceNotFoundException")
        void when_collection_not_found_expect_resource_not_found_exception() {
            when(collectionRepository.findById("missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> collectionService.getCollectionComics("missing"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("missing");

            verify(comicRepository, never()).findByCollectionName(any());
        }
    }

    @Nested
    @DisplayName("Create Collection Tests")
    class CreateCollectionTests {

        private final CreateCollectionDTO dto = CreateCollectionDTO.builder()
                .name("Batman").publisher("DC").description("The Dark Knight").ongoingCollection(true).build();

        @Test
        @DisplayName("When name is free, expect collection created with defaults")
        void when_name_free_expect_collection_created() {
            when(collectionRepository.existsByName("Batman")).thenReturn(false);
            when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenReturn(new CollectionResponseDTO());

            CollectionResponseDTO result = collectionService.createCollection(dto);

            assertThat(result).isNotNull();
            ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
            verify(collectionRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Batman");
            assertThat(captor.getValue().getDescription()).isEqualTo("The Dark Knight");
            assertThat(captor.getValue().getOngoingCollection()).isTrue();
            assertThat(captor.getValue().getWantToComplete()).isFalse();
        }

        @Test
        @DisplayName("When name already exists, expect DuplicateResourceException")
        void when_name_exists_expect_duplicate_exception() {
            when(collectionRepository.existsByName("Batman")).thenReturn(true);

            assertThatThrownBy(() -> collectionService.createCollection(dto))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("Batman");

            verify(collectionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Collection Tests")
    class UpdateCollectionTests {

        @Test
        @DisplayName("When renamed, expect rename propagated to its comics")
        void when_renamed_expect_propagated_to_comics() {
            Collection col = new Collection("Batman", "DC", true, true);
            col.setId("col-1");
            Comic comic = new Comic("Batman", 1, "DC", 3.99);

            CreateCollectionDTO dto = CreateCollectionDTO.builder()
                    .name("Batman Rebirth").publisher("DC").build();

            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(collectionRepository.existsByName("Batman Rebirth")).thenReturn(false);
            when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));
            when(comicRepository.findByCollectionName("Batman")).thenReturn(List.of(comic));
            when(comicRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
            when(comicRepository.countByCollectionName("Batman Rebirth")).thenReturn(1L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenReturn(new CollectionResponseDTO());

            collectionService.updateCollection("col-1", dto);

            assertThat(comic.getCollectionName()).isEqualTo("Batman Rebirth");
            verify(comicRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("When new name is taken, expect DuplicateResourceException")
        void when_new_name_taken_expect_duplicate_exception() {
            Collection col = new Collection("Batman", "DC", true, true);
            col.setId("col-1");
            CreateCollectionDTO dto = CreateCollectionDTO.builder().name("Superman").publisher("DC").build();

            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(collectionRepository.existsByName("Superman")).thenReturn(true);

            assertThatThrownBy(() -> collectionService.updateCollection("col-1", dto))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(collectionRepository, never()).save(any());
        }

        @Test
        @DisplayName("When collection not found, expect ResourceNotFoundException")
        void when_collection_not_found_expect_resource_not_found_exception() {
            when(collectionRepository.findById("missing")).thenReturn(Optional.empty());
            CreateCollectionDTO dto = CreateCollectionDTO.builder().name("X").publisher("Y").build();

            assertThatThrownBy(() -> collectionService.updateCollection("missing", dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete Collection Tests")
    class DeleteCollectionTests {

        @Test
        @DisplayName("When collection has no comics, expect deleted")
        void when_no_comics_expect_deleted() {
            Collection col = new Collection("Batman", "DC", true, true);
            col.setId("col-1");
            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(comicRepository.countByCollectionName("Batman")).thenReturn(0L);

            collectionService.deleteCollection("col-1");

            verify(collectionRepository).deleteById("col-1");
        }

        @Test
        @DisplayName("When collection still has comics, expect ValidationException")
        void when_has_comics_expect_validation_exception() {
            Collection col = new Collection("Batman", "DC", true, true);
            col.setId("col-1");
            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(comicRepository.countByCollectionName("Batman")).thenReturn(4L);

            assertThatThrownBy(() -> collectionService.deleteCollection("col-1"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("4");

            verify(collectionRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("When collection not found, expect ResourceNotFoundException")
        void when_collection_not_found_expect_resource_not_found_exception() {
            when(collectionRepository.findById("missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> collectionService.deleteCollection("missing"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Toggle WantToComplete Tests")
    class ToggleWantToCompleteTests {

        private Collection collection(Boolean wantToComplete) {
            Collection col = new Collection("Batman", "DC", true, wantToComplete);
            col.setId("col-1");
            return col;
        }

        @Test
        @DisplayName("When wantToComplete is true, expect toggled to false")
        void when_want_to_complete_true_expect_toggled_to_false() {
            Collection col = collection(true);
            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));
            when(comicRepository.countByCollectionName("Batman")).thenReturn(4L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenReturn(new CollectionResponseDTO());

            collectionService.toggleWantToComplete("col-1");

            ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
            verify(collectionRepository).save(captor.capture());
            assertThat(captor.getValue().getWantToComplete()).isFalse();
        }

        @Test
        @DisplayName("When wantToComplete is false, expect toggled to true")
        void when_want_to_complete_false_expect_toggled_to_true() {
            Collection col = collection(false);
            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));
            when(comicRepository.countByCollectionName("Batman")).thenReturn(4L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenReturn(new CollectionResponseDTO());

            collectionService.toggleWantToComplete("col-1");

            ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
            verify(collectionRepository).save(captor.capture());
            assertThat(captor.getValue().getWantToComplete()).isTrue();
        }

        @Test
        @DisplayName("When wantToComplete is null, expect toggled to true")
        void when_want_to_complete_null_expect_toggled_to_true() {
            Collection col = collection(null);
            when(collectionRepository.findById("col-1")).thenReturn(Optional.of(col));
            when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));
            when(comicRepository.countByCollectionName("Batman")).thenReturn(4L);
            when(entityMapper.toCollectionResponseDTO(any(Collection.class), anyLong(), anyLong()))
                    .thenReturn(new CollectionResponseDTO());

            collectionService.toggleWantToComplete("col-1");

            ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
            verify(collectionRepository).save(captor.capture());
            assertThat(captor.getValue().getWantToComplete()).isTrue();
        }

        @Test
        @DisplayName("When collection not found, expect ResourceNotFoundException")
        void when_collection_not_found_expect_resource_not_found_exception() {
            when(collectionRepository.findById("missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> collectionService.toggleWantToComplete("missing"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("missing");

            verify(collectionRepository, never()).save(any());
        }
    }
}
