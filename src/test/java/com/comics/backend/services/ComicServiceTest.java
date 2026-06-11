package com.comics.backend.services;

import com.comics.backend.dto.ComicResponseDTO;
import com.comics.backend.dto.CreateComicDTO;
import com.comics.backend.exceptions.DuplicateResourceException;
import com.comics.backend.exceptions.ResourceNotFoundException;
import com.comics.backend.models.Comic;
import com.comics.backend.mappers.EntityMapper;
import com.comics.backend.repository.ComicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComicService Tests")
class ComicServiceTest {

    private ComicService comicService;

    @Mock
    private ComicRepository comicRepository;

    @Mock
    private EntityMapper entityMapper;

    @BeforeEach
    void beforeEach() {
        comicService = new ComicService(comicRepository, entityMapper);
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {
        @Test
        @DisplayName("When repository provided, expect service initialized")
        void when_repository_provided_expect_service_initialized() {
            assertThat(comicService).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get All Comics Tests")
    class GetAllComicsTests {
        @Test
        @DisplayName("When comics exist with pagination, expect all comics returned")
        void when_comics_exist_expect_all_comics_returned() {
            Comic comic1 = new Comic("Spider-Man", 1, "Marvel", 3.99);
            Comic comic2 = new Comic("Batman", 1, "DC", 3.99);
            
            Page<Comic> page = new PageImpl<>(List.of(comic1, comic2));
            Pageable pageable = PageRequest.of(0, 20);
            
            ComicResponseDTO dto1 = new ComicResponseDTO();
            ComicResponseDTO dto2 = new ComicResponseDTO();
            
            when(comicRepository.findAll(pageable)).thenReturn(page);
            when(entityMapper.toComicResponseDTO(comic1)).thenReturn(dto1);
            when(entityMapper.toComicResponseDTO(comic2)).thenReturn(dto2);

            Page<ComicResponseDTO> result = comicService.getAllComics(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            verify(comicRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("When no comics exist, expect empty page")
        void when_no_comics_exist_expect_empty_page() {
            Page<Comic> emptyPage = new PageImpl<>(List.of());
            Pageable pageable = PageRequest.of(0, 20);
            
            when(comicRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<ComicResponseDTO> result = comicService.getAllComics(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("When getting all comics without pagination, expect list returned")
        void when_getting_all_comics_without_pagination_expect_list_returned() {
            Comic comic1 = new Comic("Spider-Man", 1, "Marvel", 3.99);
            Comic comic2 = new Comic("Batman", 1, "DC", 3.99);
            List<Comic> comics = List.of(comic1, comic2);
            
            ComicResponseDTO dto1 = new ComicResponseDTO();
            ComicResponseDTO dto2 = new ComicResponseDTO();
            
            when(comicRepository.findAll()).thenReturn(comics);
            when(entityMapper.toComicResponseDTO(comic1)).thenReturn(dto1);
            when(entityMapper.toComicResponseDTO(comic2)).thenReturn(dto2);

            List<ComicResponseDTO> result = comicService.getAllComics();

            assertThat(result).isNotNull().hasSize(2);
            verify(comicRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Create Comic Tests")
    class CreateComicTests {
        @Test
        @DisplayName("When valid comic, expect comic created successfully")
        void when_valid_comic_expect_comic_created() {
            CreateComicDTO dto = new CreateComicDTO();
            dto.setTitle("Spider-Man");
            dto.setNumber(1);
            dto.setPublisher("Marvel");
            dto.setPrice(3.99);
            
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            ComicResponseDTO responseDTO = new ComicResponseDTO();
            
            when(comicRepository.findByTitleAndNumber("Spider-Man", 1)).thenReturn(Optional.empty());
            when(entityMapper.toComicEntity(dto)).thenReturn(comic);
            when(comicRepository.save(any(Comic.class))).thenReturn(comic);
            when(entityMapper.toComicResponseDTO(comic)).thenReturn(responseDTO);

            ComicResponseDTO result = comicService.createComic(dto);

            assertThat(result).isNotNull();
            verify(comicRepository, times(1)).save(any(Comic.class));
        }

        @Test
        @DisplayName("When duplicate comic, expect exception thrown")
        void when_duplicate_comic_expect_exception() {
            CreateComicDTO dto = new CreateComicDTO();
            dto.setTitle("Spider-Man");
            dto.setNumber(1);
            dto.setPublisher("Marvel");
            dto.setPrice(3.99);
            
            Comic existingComic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            when(comicRepository.findByTitleAndNumber("Spider-Man", 1))
                    .thenReturn(Optional.of(existingComic));

            assertThatThrownBy(() -> comicService.createComic(dto))
                    .isInstanceOf(DuplicateResourceException.class);
            
            verify(comicRepository, never()).save(any());
        }

        @Test
        @DisplayName("When blank title, expect exception thrown")
        void when_blank_title_expect_exception() {
            CreateComicDTO dto = new CreateComicDTO();
            dto.setTitle("");

            assertThatThrownBy(() -> comicService.createComic(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("title");
        }
    }

    @Nested
    @DisplayName("Get Comic By ID Tests")
    class GetComicByIdTests {
        @Test
        @DisplayName("When comic exists, expect comic returned")
        void when_comic_exists_expect_comic_returned() {
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            comic.setId("123");
            ComicResponseDTO dto = new ComicResponseDTO();
            
            when(comicRepository.findById("123")).thenReturn(Optional.of(comic));
            when(entityMapper.toComicResponseDTO(comic)).thenReturn(dto);

            ComicResponseDTO result = comicService.getComicById("123");

            assertThat(result).isNotNull();
            verify(comicRepository, times(1)).findById("123");
        }

        @Test
        @DisplayName("When comic not found, expect exception thrown")
        void when_comic_not_found_expect_exception() {
            when(comicRepository.findById("999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> comicService.getComicById("999"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("When blank ID, expect exception thrown")
        void when_blank_id_expect_exception() {
            assertThatThrownBy(() -> comicService.getComicById(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Search Comics Tests")
    class SearchComicsTests {
        @Test
        @DisplayName("When searching partial title, expect matching comics returned")
        void when_searching_partial_title_expect_matching_comics() {
            Comic comic1 = new Comic("Avengers", 1, "Marvel", 3.99);
            Comic comic2 = new Comic("Avengers 2", 1, "Marvel", 3.99);
            List<Comic> comics = List.of(comic1, comic2);
            
            ComicResponseDTO dto1 = new ComicResponseDTO();
            ComicResponseDTO dto2 = new ComicResponseDTO();
            
            when(comicRepository.findByTitleContainsIgnoreCase("avenge"))
                    .thenReturn(comics);
            when(entityMapper.toComicResponseDTO(comic1)).thenReturn(dto1);
            when(entityMapper.toComicResponseDTO(comic2)).thenReturn(dto2);

            List<ComicResponseDTO> result = comicService.searchComicsByTitle("avenge");

            assertThat(result).isNotNull().hasSize(2);
            verify(comicRepository, times(1)).findByTitleContainsIgnoreCase("avenge");
        }

        @Test
        @DisplayName("When search no results, expect exception thrown")
        void when_search_no_results_expect_exception() {
            when(comicRepository.findByTitleContainsIgnoreCase("nonexistent"))
                    .thenReturn(List.of());

            assertThatThrownBy(() -> comicService.searchComicsByTitle("nonexistent"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("When blank search term, expect exception thrown")
        void when_blank_search_term_expect_exception() {
            assertThatThrownBy(() -> comicService.searchComicsByTitle(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Get Comic By Title Tests")
    class GetComicByTitleTests {
        @Test
        @DisplayName("When comic with exact title exists, expect comic returned")
        void when_exact_title_exists_expect_comic_returned() {
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            List<Comic> comics = List.of(comic);
            ComicResponseDTO dto = new ComicResponseDTO();
            
            when(comicRepository.findByTitleContainsIgnoreCase("Spider-Man"))
                    .thenReturn(comics);
            when(entityMapper.toComicResponseDTO(comic)).thenReturn(dto);

            ComicResponseDTO result = comicService.getComicByTitle("Spider-Man");

            assertThat(result).isNotNull();
            verify(comicRepository, times(1)).findByTitleContainsIgnoreCase("Spider-Man");
        }

        @Test
        @DisplayName("When multiple comics with title, expect first returned")
        void when_multiple_comics_expect_first_returned() {
            Comic comic1 = new Comic("Avengers", 1, "Marvel", 3.99);
            Comic comic2 = new Comic("Avengers", 2, "Marvel", 3.99);
            List<Comic> comics = List.of(comic1, comic2);
            ComicResponseDTO dto = new ComicResponseDTO();
            
            when(comicRepository.findByTitleContainsIgnoreCase("Avengers"))
                    .thenReturn(comics);
            when(entityMapper.toComicResponseDTO(comic1)).thenReturn(dto);

            ComicResponseDTO result = comicService.getComicByTitle("Avengers");

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Update Comic Tests")
    class UpdateComicTests {
        @Test
        @DisplayName("When valid update, expect comic updated")
        void when_valid_update_expect_comic_updated() {
            String id = "123";
            Comic existingComic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            existingComic.setId(id);
            
            CreateComicDTO updateDTO = new CreateComicDTO();
            updateDTO.setTitle("Spider-Man 2");
            updateDTO.setNumber(2);
            updateDTO.setPublisher("Marvel");
            updateDTO.setPrice(4.99);
            
            ComicResponseDTO responseDTO = new ComicResponseDTO();
            
            when(comicRepository.findById(id)).thenReturn(Optional.of(existingComic));
            when(comicRepository.findByTitleAndNumber("Spider-Man 2", 2))
                    .thenReturn(Optional.empty());
            when(comicRepository.save(any(Comic.class))).thenReturn(existingComic);
            when(entityMapper.toComicResponseDTO(any())).thenReturn(responseDTO);

            ComicResponseDTO result = comicService.updateComic(id, updateDTO);

            assertThat(result).isNotNull();
            verify(comicRepository, times(1)).save(any(Comic.class));
        }

        @Test
        @DisplayName("When comic not found, expect exception thrown")
        void when_comic_not_found_expect_exception() {
            CreateComicDTO updateDTO = new CreateComicDTO();
            when(comicRepository.findById("999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> comicService.updateComic("999", updateDTO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete Comic Tests")
    class DeleteComicTests {
        @Test
        @DisplayName("When comic exists, expect comic deleted")
        void when_comic_exists_expect_comic_deleted() {
            String id = "123";
            when(comicRepository.existsById(id)).thenReturn(true);

            comicService.deleteComic(id);

            verify(comicRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("When comic not exists, expect exception thrown")
        void when_comic_not_exists_expect_exception() {
            when(comicRepository.existsById("999")).thenReturn(false);

            assertThatThrownBy(() -> comicService.deleteComic("999"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Deactivate Comic Tests")
    class DeactivateComicTests {
        @Test
        @DisplayName("When valid comic, expect comic deactivated")
        void when_valid_comic_expect_deactivated() {
            String id = "123";
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            comic.setId(id);
            comic.setActive(true);
            
            ComicResponseDTO responseDTO = new ComicResponseDTO();
            
            when(comicRepository.findById(id)).thenReturn(Optional.of(comic));
            when(comicRepository.save(any(Comic.class))).thenReturn(comic);
            when(entityMapper.toComicResponseDTO(any())).thenReturn(responseDTO);

            ComicResponseDTO result = comicService.deactivateComic(id);

            assertThat(result).isNotNull();
            verify(comicRepository, times(1)).save(any(Comic.class));
        }
    }

    @Nested
    @DisplayName("Update Stock Tests")
    class UpdateStockTests {
        @Test
        @DisplayName("When valid stock update, expect stock updated")
        void when_valid_stock_update_expect_updated() {
            String id = "123";
            Comic comic = new Comic("Spider-Man", 1, "Marvel", 3.99);
            comic.setId(id);
            comic.setStock(5);
            
            ComicResponseDTO responseDTO = new ComicResponseDTO();
            
            when(comicRepository.findById(id)).thenReturn(Optional.of(comic));
            when(comicRepository.save(any(Comic.class))).thenReturn(comic);
            when(entityMapper.toComicResponseDTO(any())).thenReturn(responseDTO);

            ComicResponseDTO result = comicService.updateComicStock(id, 10);

            assertThat(result).isNotNull();
            verify(comicRepository, times(1)).save(any(Comic.class));
        }
    }
}

