package com.interswitch.StyleMe.controller;

import com.interswitch.StyleMe.dto.requests.ConfirmationCode;
import com.interswitch.StyleMe.dto.requests.ConfirmationRequest;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.service.UserService;
import com.interswitch.StyleMe.util.EmailMessageSender;
import com.interswitch.StyleMe.util.VerificationCodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/confirm")
@Tag(name = "ConfirmationController", description = "CRUD REST APIs For User Email Confirmation")
@RequiredArgsConstructor
public class ConfirmationController {

    private final VerificationCodeUtil verificationCodeUtil;

    private final UserService userService;

    @Operation(summary = "Request account confirmation via email.", tags = "UserController")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/account/email")
    public ResponseEntity<String> confirmationViaEmail(@Valid @RequestBody ConfirmationRequest confirmationRequest)
            throws StyleMeException {
        return ResponseEntity.ok(userService.ResendVerificationToken(confirmationRequest.getEmail()));
    }

    @Operation(summary = "Code confirmation by client email.", tags = "UserController")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/code")
    public ResponseEntity<String> codeConfirmation(@Valid @RequestBody ConfirmationCode confirmationCode)
            throws StyleMeException {
        Boolean isVerified = verificationCodeUtil.verifyCode(
                confirmationCode.getEmail(), confirmationCode.getCode());
        if (isVerified) {
            return new ResponseEntity<>("Code Confirmed.", HttpStatus.OK);
        }
        throw new StyleMeException("The verification code was not found or expired.", HttpStatus.NOT_FOUND);
    }
}
