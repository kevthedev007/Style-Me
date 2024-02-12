package com.interswitch.StyleMe.service;

import com.interswitch.StyleMe.dto.UserDto;
import com.interswitch.StyleMe.dto.requests.CreateAccountRequest;
import com.interswitch.StyleMe.dto.requests.ForgotPasswordRequest;
import com.interswitch.StyleMe.dto.requests.OauthLoginRequest;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.model.User;

public interface UserService {

    UserDto createUserAccount( CreateAccountRequest createAccountRequest) throws StyleMeException;

    User findUserByEmail(String email) throws StyleMeException;

    String ResendVerificationToken(String email) throws StyleMeException;

    void processOauthLoginRequest(OauthLoginRequest oauthLoginRequest) throws StyleMeException;

    void changePassword(ForgotPasswordRequest forgotPasswordRequest) throws StyleMeException;
}
