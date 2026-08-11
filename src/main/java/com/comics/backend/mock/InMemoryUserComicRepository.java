package com.comics.backend.mock;

import com.comics.backend.models.UserComic;
import com.comics.backend.repository.UserComicRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Primary
@Profile("mock")
@Slf4j
public class InMemoryUserComicRepository implements UserComicRepository {

    private static final String PERSIST_FILE = "mock-data/user-comics.json";

    private final Map<String, UserComic> store = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final ObjectMapper mapper;

    public InMemoryUserComicRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    void load() {
        File file = new File(PERSIST_FILE);
        if (!file.exists()) return;
        try {
            Map<String, UserComic> loaded = mapper.readValue(file, new TypeReference<>() {});
            store.putAll(loaded);
            // restart counter above max existing id
            store.keySet().stream()
                    .filter(k -> k.startsWith("mock-uc-"))
                    .map(k -> k.replace("mock-uc-", ""))
                    .mapToInt(s -> { try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; } })
                    .max().ifPresent(max -> idCounter.set(max + 1));
            log.info("Loaded {} user-comic entries from {}", store.size(), PERSIST_FILE);
        } catch (Exception e) {
            log.warn("Could not load {}: {}", PERSIST_FILE, e.getMessage());
        }
    }

    private void persist() {
        try {
            File file = new File(PERSIST_FILE);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, store);
        } catch (Exception e) {
            log.warn("Could not persist user-comics: {}", e.getMessage());
        }
    }

    private String nextId() {
        return "mock-uc-" + idCounter.getAndIncrement();
    }

    @Override
    public List<UserComic> findByUserId(String userId) {
        return store.values().stream()
                .filter(uc -> userId.equals(uc.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserComic> findByUserIdAndComicId(String userId, String comicId) {
        return store.values().stream()
                .filter(uc -> userId.equals(uc.getUserId()) && comicId.equals(uc.getComicId()))
                .findFirst();
    }

    @Override
    public boolean existsByUserIdAndComicId(String userId, String comicId) {
        return store.values().stream()
                .anyMatch(uc -> userId.equals(uc.getUserId()) && comicId.equals(uc.getComicId()));
    }

    @Override
    public void deleteByUserIdAndComicId(String userId, String comicId) {
        store.values().removeIf(uc -> userId.equals(uc.getUserId()) && comicId.equals(uc.getComicId()));
        persist();
    }

    @Override
    public <S extends UserComic> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId());
        }
        store.put(entity.getId(), entity);
        persist();
        return entity;
    }

    @Override
    public <S extends UserComic> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        entities.forEach(e -> {
            if (e.getId() == null) e.setId(nextId());
            store.put(e.getId(), e);
            result.add(e);
        });
        persist();
        return result;
    }

    @Override
    public Optional<UserComic> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    @Override
    public List<UserComic> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<UserComic> findAllById(Iterable<String> ids) {
        Set<String> idSet = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toSet());
        return store.values().stream().filter(uc -> idSet.contains(uc.getId())).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
        persist();
    }

    @Override
    public void delete(UserComic entity) {
        store.remove(entity.getId());
        persist();
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(store::remove);
        persist();
    }

    @Override
    public void deleteAll(Iterable<? extends UserComic> entities) {
        entities.forEach(e -> store.remove(e.getId()));
        persist();
    }

    @Override
    public void deleteAll() {
        store.clear();
        persist();
    }

    @Override
    public List<UserComic> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<UserComic> findAll(Pageable pageable) {
        List<UserComic> all = findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<UserComic> content = (start < end) ? all.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, all.size());
    }

    @Override
    public <S extends UserComic> S insert(S entity) {
        return save(entity);
    }

    @Override
    public <S extends UserComic> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public <S extends UserComic> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends UserComic> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends UserComic> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends UserComic> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends UserComic> long count(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends UserComic> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends UserComic, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }
}
