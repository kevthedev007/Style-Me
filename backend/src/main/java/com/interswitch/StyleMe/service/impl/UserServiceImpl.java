package com.interswitch.StyleMe.service.impl;

import com.interswitch.StyleMe.dto.UserDto;
import com.interswitch.StyleMe.dto.requests.CreateAccountRequest;
import com.interswitch.StyleMe.dto.requests.ForgotPasswordRequest;
import com.interswitch.StyleMe.dto.requests.OauthLoginRequest;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.enums.Provider;
import com.interswitch.StyleMe.enums.RoleType;
import com.interswitch.StyleMe.model.User;
import com.interswitch.StyleMe.model.VerificationCode;
import com.interswitch.StyleMe.repository.UserRepository;
import com.interswitch.StyleMe.repository.VerificationCodeRepository;
import com.interswitch.StyleMe.service.UserService;
import com.interswitch.StyleMe.util.EmailMessageSender;
import com.interswitch.StyleMe.util.VerificationCodeUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final EmailMessageSender messageSender;

    private final VerificationCodeUtil verificationCodeUtil;

    private final VerificationCodeRepository verificationCodeRepository;


    @Override
    @Transactional
    public UserDto createUserAccount(CreateAccountRequest createAccountRequest) throws StyleMeException {
        validateIfUserExist(createAccountRequest);

        User user = new User();
        user.setEmail(createAccountRequest.getEmail());
        user.setFirstName(createAccountRequest.getFirstName());
        user.setLastName(createAccountRequest.getLastName());
        user.setPassword(bCryptPasswordEncoder.encode(createAccountRequest.getPassword()));
        user.setRoles(RoleType.ROLE_USER);

        User savedUser = userRepository.save(user);

        int codeGenerated = verificationCodeUtil.generateCode();

        messageSender.sendVerificationCode(savedUser.getEmail(), codeGenerated);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(codeGenerated);
        verificationCode.setUser(savedUser);
        verificationCode.setExpiredTime(LocalDateTime.now().plusSeconds(300));

        verificationCodeRepository.save(verificationCode);

        return modelMapper.map(savedUser, UserDto.class);
    }

    private void validateIfUserExist(CreateAccountRequest createAccountRequest) throws StyleMeException {
        Optional<User> user = userRepository.findUserByEmail(createAccountRequest.getEmail());
        if (user.isPresent()) {
            throw new StyleMeException("User already exist.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public User findUserByEmail(String email) throws StyleMeException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new StyleMeException("user does not exist", HttpStatus.NOT_FOUND));
    }

    @Override
    public String ResendVerificationToken(String email) throws StyleMeException {
        User user = findUserByEmail(email);

        if (user.isEmailVerified()) return "Email has already been verified.";

        int codeGenerated = verificationCodeUtil.generateCode();

        messageSender.sendVerificationCode(email, codeGenerated);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(codeGenerated);
        verificationCode.setUser(user);
        verificationCode.setExpiredTime(LocalDateTime.now().plusSeconds(300));

        verificationCodeRepository.save(verificationCode);

        return "Verification Token has been sent to your mail.";
    }


    @Override
    public void processOauthLoginRequest(OauthLoginRequest oauthLoginRequest) throws StyleMeException {
        User user = findUserByEmail(oauthLoginRequest.getEmail());
        if (user != null && user.getProvider() != Provider.GOOGLE) {
            throw new StyleMeException("wrong login path", HttpStatus.BAD_REQUEST);
        }
        if (user == null) {
            User newUser = new User();
            newUser.setEmail(oauthLoginRequest.getEmail());
            newUser.setFirstName(oauthLoginRequest.getFirstName());
            newUser.setLastName(oauthLoginRequest.getLastName());
            newUser.setProvider(Provider.GOOGLE);
            userRepository.save(newUser);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(), getAuthorities(Collections.singleton(RoleType.ROLE_USER)));
        }
        return null;
    }

    @Override
    public void changePassword(ForgotPasswordRequest forgotPasswordRequest) throws StyleMeException {
        User user = findUserByEmail(forgotPasswordRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(forgotPasswordRequest.getPassword()));
        userRepository.save(user);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<RoleType> roles) {
        return roles.stream().map(
                role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toSet());
    }
}
