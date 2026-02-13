package com.comics.backend.controllers;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import com.comics.backend.models.Comic;
import com.comics.backend.services.ComicService;

import java.util.List;

@RestController
@RequestMapping("/comics")
public class ComicController {

    private final ComicService comicService;

    public ComicController(ComicService comicService) {
        this.comicService = comicService;
    }

    // Obtener todos los comics
    @GetMapping
    public List<Comic> getComics() {
        return comicService.getAllComics();
    }

    // Crear un comic
    @PostMapping
    public Comic addComic(@RequestBody Comic comic) {
        return comicService.createComic(comic);
    }

    // Obtener comic por título
    @GetMapping("/title/{title}")
    public Comic getComicByTitle(@PathVariable String title) {
        return comicService.getComicByTitle(title);
    }

    // Eliminar comic por id
    @DeleteMapping("/{id}")
    public void deleteComic(@PathVariable @NonNull String id) {
        comicService.deleteComic(id);
    }
}
