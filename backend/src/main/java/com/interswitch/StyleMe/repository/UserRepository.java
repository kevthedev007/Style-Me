package com.interswitch.StyleMe.repository;

import com.interswitch.StyleMe.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findByEmail(String email);
}
