package com.interswitch.StyleMe.model;

import com.interswitch.StyleMe.enums.Provider;
import com.interswitch.StyleMe.enums.RoleType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "users")
@ToString
public class User {

    private String id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private RoleType roles = RoleType.ROLE_USER;

    private Provider provider = Provider.DEFAULT;

    @Indexed(name = "email_verified")
    private boolean isEmailVerified;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;


}
