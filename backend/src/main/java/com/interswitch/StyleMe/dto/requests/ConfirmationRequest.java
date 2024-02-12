package com.interswitch.StyleMe.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ConfirmationRequest {
    @NotEmpty(message = "Email cannot be null.")
    @Email(message = "Email should be valid.")
    private String email;
}
