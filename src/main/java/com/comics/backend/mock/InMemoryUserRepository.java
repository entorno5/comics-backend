package com.comics.backend.mock;

import com.comics.backend.models.User;
import com.comics.backend.repository.UserRepository;
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
 * In-memory implementation of UserRepository for the "mock" Spring profile.
 * Allows the application to start and respond to HTTP requests without a real MongoDB instance.
 * Data is stored in a ConcurrentHashMap and is lost on application restart.
 */
@Repository
@Primary
@Profile("mock")
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    private String nextId() {
        return "mock-user-" + idCounter.getAndIncrement();
    }

    // ── Custom query methods ──────────────────────────────────────────────────

    @Override
    public Optional<User> findByNickname(String nickname) {
        return store.values().stream()
                .filter(u -> u.getNickname().equals(nickname))
                .findFirst();
    }

    @Override
    public Optional<User> findByMail(String mail) {
        return store.values().stream()
                .filter(u -> u.getMail().equals(mail))
                .findFirst();
    }

    // ── CrudRepository ────────────────────────────────────────────────────────

    @Override
    public <S extends User> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        entities.forEach(e -> result.add(save(e)));
        return result;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<User> findAllById(Iterable<String> ids) {
        Set<String> idSet = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toSet());
        return store.values().stream()
                .filter(u -> idSet.contains(u.getId()))
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
    public void delete(User entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(store::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(e -> store.remove(e.getId()));
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

    // ── PagingAndSortingRepository ────────────────────────────────────────────

    @Override
    public List<User> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> all = findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<User> content = (start < end) ? all.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, all.size());
    }

    // ── MongoRepository insert shortcuts ─────────────────────────────────────

    @Override
    public <S extends User> S insert(S entity) {
        return save(entity);
    }

    @Override
    public <S extends User> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    // ── QueryByExampleExecutor (not used by services, unsupported in mock) ────

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("QueryByExample not supported in mock profile");
    }
}
