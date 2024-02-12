package com.interswitch.StyleMe.controller;

import com.interswitch.StyleMe.dto.UserDto;
import com.interswitch.StyleMe.dto.requests.CreateAccountRequest;
import com.interswitch.StyleMe.dto.requests.ForgotPasswordRequest;
import com.interswitch.StyleMe.dto.requests.LoginRequest;
import com.interswitch.StyleMe.dto.responses.ApiResponse;
import com.interswitch.StyleMe.dto.responses.LoginResponseDto;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.model.User;
import com.interswitch.StyleMe.security.jwt.TokenProvider;
import com.interswitch.StyleMe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "UserController", description = "CRUD REST APIs For Users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    @Operation(summary = "REST API to Signup User on the application.", tags = "UserController")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody CreateAccountRequest createAccountRequest)
            throws StyleMeException {
        UserDto userDto = userService.createUserAccount(createAccountRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .data(userDto)
                .status("success")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "REST API to Login User to the application.", tags = "UserController")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) throws StyleMeException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = userService.findUserByEmail(loginRequest.getEmail());
        if (!user.isEmailVerified()) {
            throw new StyleMeException("Email is not verified", HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateJWTToken(authentication);
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setFirstName(user.getFirstName());
        loginResponseDto.setLastName(user.getLastName());
        loginResponseDto.setEmail(user.getEmail());
        loginResponseDto.setToken(token);
        return ResponseEntity.ok(loginResponseDto);
    }


    @Operation(summary = "REST API to enable Users change password.", tags = "UserController")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PutMapping("change-password")
    @ResponseStatus(HttpStatus.CREATED)
    public void resetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws StyleMeException {
        userService.changePassword(forgotPasswordRequest);
    }
}
