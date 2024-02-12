package com.interswitch.StyleMe.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class OauthLoginRequest {
    private String firstName;
    private String lastName;
    @NotBlank(message = "email can not be blank")
    private String email;
}
