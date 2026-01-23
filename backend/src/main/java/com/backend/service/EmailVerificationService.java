package com.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "auth:email:";

    public void sendVerificationCode(String email) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        redisTemplate.opsForValue().set(PREFIX + email, code, Duration.ofMinutes(5));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[도로교통 커뮤니티] 회원가입 인증 코드");
        message.setText("인증 코드: " + code);
        mailSender.send(message);
    }

    public void verifyCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(PREFIX + email);
        if (storedCode == null || !storedCode.equals(inputCode)) {
            throw new IllegalArgumentException("인증 코드가 유효하지 않습니다.");
        }
        redisTemplate.delete(PREFIX + email);
    }
}