package com.email.email.writer.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.email.email.writer.model.User;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

}