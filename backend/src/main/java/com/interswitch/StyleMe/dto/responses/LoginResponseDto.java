package com.interswitch.StyleMe.dto.responses;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String token;
}
