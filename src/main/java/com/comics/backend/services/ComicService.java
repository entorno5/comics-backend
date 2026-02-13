package com.comics.backend.services;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.comics.backend.models.Comic;
import com.comics.backend.repository.ComicRepository;

import java.util.List;

@Service
public class ComicService {

    private final ComicRepository comicRepository;

    public ComicService(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    // Obtener todos los comics
    public List<Comic> getAllComics() {
        return comicRepository.findAll();
    }

    // Crear un comic
    public Comic createComic(Comic comic) {
        if (comicRepository.findByTitle(comic.getTitle()).isPresent()) {
            throw new RuntimeException("Comic con ese título ya existe");
        }
        return comicRepository.save(comic);
    }

    // Obtener comic por título
    public Comic getComicByTitle(String title) {
        return comicRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Comic no encontrado"));
    }

    // Eliminar comic por id
    public void deleteComic(@NonNull String id) {
        if (!comicRepository.existsById(id)) {
            throw new RuntimeException("Comic no encontrado");
        }
        comicRepository.deleteById(id);
    }
}
