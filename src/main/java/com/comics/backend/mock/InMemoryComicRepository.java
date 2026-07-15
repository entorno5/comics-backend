package com.comics.backend.mock;

import com.comics.backend.models.Comic;
import com.comics.backend.repository.ComicRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * In-memory implementation of ComicRepository for the "mock" Spring profile.
 * Allows the application to start and respond to HTTP requests without a real MongoDB instance.
 * Data is stored in a ConcurrentHashMap and is lost on application restart.
 */
@Repository
@Primary
@Profile("mock")
public class InMemoryComicRepository implements ComicRepository {

    private final Map<String, Comic> store = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    private String nextId() {
        return "mock-comic-" + idCounter.getAndIncrement();
    }

    // ── Custom query methods ──────────────────────────────────────────────────

    @Override
    public Optional<Comic> findByTitleAndNumber(String title, int number) {
        return store.values().stream()
                .filter(c -> c.getTitle().equals(title) && c.getNumber() == number)
                .findFirst();
    }

    @Override
    public List<Comic> findByTitle(String title) {
        return store.values().stream()
                .filter(c -> c.getTitle().equals(title))
                .collect(Collectors.toList());
    }

    @Override
    public List<Comic> findByCollectionName(String collectionName) {
        return store.values().stream()
                .filter(c -> collectionName.equals(c.getCollectionName()))
                .collect(Collectors.toList());
    }

    @Override
    public long countByCollectionName(String collectionName) {
        return store.values().stream()
                .filter(c -> collectionName.equals(c.getCollectionName()))
                .count();
    }

    @Override
    public List<Comic> findByTitleContainsIgnoreCase(String titleSearch) {
        return store.values().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(titleSearch.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Comic> findByTitleContainsIgnoreCasePaged(String titleSearch, Pageable pageable) {
        List<Comic> filtered = store.values().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(titleSearch.toLowerCase()))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Comic> content = (start < end) ? filtered.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, filtered.size());
    }

    // ── CrudRepository ────────────────────────────────────────────────────────

    @Override
    public <S extends Comic> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Comic> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        entities.forEach(e -> result.add(save(e)));
        return result;
    }

    @Override
    public Optional<Comic> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    @Override
    public List<Comic> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(
                        c -> c.getPublishedDate() != null ? c.getPublishedDate() : LocalDate.MIN,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Comic> findAllById(Iterable<String> ids) {
        Set<String> idSet = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toSet());
        return store.values().stream()
                .filter(c -> idSet.contains(c.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public void delete(Comic entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(store::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends Comic> entities) {
        entities.forEach(e -> store.remove(e.getId()));
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

    // ── PagingAndSortingRepository ────────────────────────────────────────────

    @Override
    public List<Comic> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Comic> findAll(Pageable pageable) {
        List<Comic> all = findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Comic> content = (start < end) ? all.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, all.size());
    }

    // ── MongoRepository insert shortcuts ─────────────────────────────────────

    @Override
    public <S extends Comic> S insert(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Comic> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    // ── QueryByExampleExecutor (not used by services, unsupported in mock) ────

    @Override
    public <S extends Comic> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Comic> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Comic> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Comic> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Comic> long count(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Comic> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Comic, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }
}
