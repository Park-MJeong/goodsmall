package com.hanghae.userservice.domain;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

}
