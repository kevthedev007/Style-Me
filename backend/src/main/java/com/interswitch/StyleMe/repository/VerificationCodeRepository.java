package com.interswitch.StyleMe.repository;

import com.interswitch.StyleMe.model.VerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends MongoRepository<VerificationCode, String> {
    Optional<VerificationCode> findByCodeAndUser_Email(int code, String email);
    List<VerificationCode> findByExpiredTimeBefore(LocalDateTime dateTime);
}
