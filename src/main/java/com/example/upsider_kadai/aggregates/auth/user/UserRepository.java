package com.example.upsider_kadai.aggregates.auth.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
