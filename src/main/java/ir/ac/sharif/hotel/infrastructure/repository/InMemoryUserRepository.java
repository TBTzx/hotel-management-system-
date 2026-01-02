package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.user.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> byId = new ConcurrentHashMap<>();
    private final Map<String, String> idByUsername = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        String id = idByUsername.get(username);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public synchronized void save(User user) {
        Objects.requireNonNull(user, "user");
        // enforce unique username
        String existingId = idByUsername.get(user.getUsername());
        if (existingId != null && !existingId.equals(user.getId())) {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        }
        byId.put(user.getId(), user);
        idByUsername.put(user.getUsername(), user.getId());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }

public synchronized void clearAndLoad(java.util.Collection<User> users) {
    byId.clear();
    idByUsername.clear();
    for (User u : users) {
        save(u);
    }
}
}
