package com.interswitch.StyleMe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Document
public class VerificationCode {
    @Id
    private String id;
    private int code;
    private LocalDateTime expiredTime;
    private User user;
    @CreatedDate
    private LocalDateTime createdDate;

    public Boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredTime);
    }
}
