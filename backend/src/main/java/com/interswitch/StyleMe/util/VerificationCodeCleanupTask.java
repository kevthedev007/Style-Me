package com.interswitch.StyleMe.util;

import com.interswitch.StyleMe.model.VerificationCode;
import com.interswitch.StyleMe.repository.VerificationCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class VerificationCodeCleanupTask {

    private final VerificationCodeRepository verificationCodeRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredVerificationCodes() {
        LocalDateTime now = LocalDateTime.now();
        List<VerificationCode> expiredCodes = verificationCodeRepository.findByExpiredTimeBefore(now);
        verificationCodeRepository.deleteAll(expiredCodes);
    }

}
