package com.comics.backend.mock;

import com.comics.backend.models.Collection;
import com.comics.backend.repository.CollectionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * In-memory implementation of CollectionRepository for the "mock" Spring profile.
 */
@Repository
@Primary
@Profile("mock")
public class InMemoryCollectionRepository implements CollectionRepository {

    private final Map<String, Collection> store = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    private String nextId() {
        return "mock-collection-" + idCounter.getAndIncrement();
    }

    @Override
    public Optional<Collection> findByName(String name) {
        return store.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public boolean existsByName(String name) {
        return store.values().stream().anyMatch(c -> c.getName().equals(name));
    }

    @Override
    public <S extends Collection> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Collection> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        entities.forEach(e -> result.add(save(e)));
        return result;
    }

    @Override
    public Optional<Collection> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    @Override
    public List<Collection> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Collection> findAllById(Iterable<String> ids) {
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
    public void delete(Collection entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(store::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends Collection> entities) {
        entities.forEach(e -> store.remove(e.getId()));
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public List<Collection> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Collection> findAll(Pageable pageable) {
        List<Collection> all = findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Collection> content = (start < end) ? all.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, all.size());
    }

    @Override
    public <S extends Collection> S insert(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Collection> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public <S extends Collection> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Collection> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Collection> long count(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Collection> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends Collection, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }
}
