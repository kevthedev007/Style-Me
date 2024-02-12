package com.interswitch.StyleMe.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequest {
    private String email;

   private String password;
}
