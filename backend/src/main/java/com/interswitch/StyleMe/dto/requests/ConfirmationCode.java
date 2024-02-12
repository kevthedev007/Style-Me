package com.interswitch.StyleMe.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ConfirmationCode {
    @NotEmpty(message = "Email cannot be null.")
    @Email(message = "Email should be valid.")
    private String email;

    @Digits(integer = 6, fraction = 0, message = "Code size must contain no more than 6 digits.")
    private int code;
}
