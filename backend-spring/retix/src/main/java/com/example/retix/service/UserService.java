package com.example.retix.service;

import com.example.retix.model.User;
import com.example.retix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheManager = "redisCacheManager")   // use the Redis cache manager bean
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @CachePut(value = "users", key = "#result.id")        // put/refresh the single‑user cache
    @CacheEvict(value = "userList", allEntries = true)    // invalidate the cached list
    public User createUser(User user) {
        User saved = userRepository.save(user);
        System.out.println("✅ Saved user in DB with ID: " + saved.getId());
        return saved;
    }

    @Cacheable(value = "users", key = "#id")              // primary cache: by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);               // will run only on cache miss
    }

    @Cacheable(value = "usersByEmail", key = "#email")    // secondary cache: by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @CachePut(value = "users", key = "#result.id")        // refresh single‑user cache
    @Caching(evict = {                                   // also clear list + email caches
        @CacheEvict(value = "userList",      allEntries = true),
        @CacheEvict(value = "usersByEmail",  key = "#result.email")
    })
    public Optional<User> updateUser(Long id, User user) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(user.getName());
            existing.setEmail(user.getEmail());
            existing.setPassword(user.getPassword());
            existing.setRole(user.getRole());
            return userRepository.save(existing);
        });
    }

    @Caching(evict = {
        @CacheEvict(value = "users",         key = "#id"),
        @CacheEvict(value = "usersByEmail",  allEntries = true),
        @CacheEvict(value = "userList",      allEntries = true)
    })
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

   
    @Cacheable("userList")                               // cache the whole list
    public List<User> getAllUsers() {
        System.out.println("Fetching all users (DB)");
        return userRepository.findAll();
    }
}
