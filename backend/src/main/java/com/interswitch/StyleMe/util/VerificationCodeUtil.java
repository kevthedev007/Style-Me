package com.interswitch.StyleMe.util;

import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.model.User;
import com.interswitch.StyleMe.model.VerificationCode;
import com.interswitch.StyleMe.repository.UserRepository;
import com.interswitch.StyleMe.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class VerificationCodeUtil {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;
    private final EmailMessageSender messageSender;

    @Value("${app.confirmation.code.expires}")
    private int expireIn;
    private static final int BOUND = 9;
    private static final int CODE_LENGTH = 6;


    public int generateCode() {
        final Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < CODE_LENGTH) {
            sb.append(random.nextInt(BOUND));
        }
        while (sb.toString().startsWith("0")) {
            sb.delete(0, 1);
            sb.append(random.nextInt(BOUND));
        }
        return Integer.parseInt(sb.toString());
    }

    public Boolean verifyCode(final String email, final int code) throws StyleMeException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new StyleMeException("User not found", HttpStatus.NOT_FOUND));

        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByCodeAndUser_Email(code, email);

        if (verificationCodeOptional.isPresent() && !verificationCodeOptional.get().isExpired()) {
            VerificationCode verificationCode = verificationCodeOptional.get();
            verificationCodeRepository.delete(verificationCode);

            messageSender.sendMessage(email, "Account Verification",
                    "Your account has been successfully verified. You can proceed to login.");

            user.setEmailVerified(true);
            userRepository.save(user);

            return true;
        }

        return false;
    }

}
