package com.backend.controller;

import com.backend.dto.auth.SignupRequestDto;
import com.backend.service.AuthService;
import com.backend.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody Map<String, String> body) {
        emailService.sendVerificationCode(body.get("email"));
        return ResponseEntity.ok("전송 완료");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.ok("가입 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = authService.login(body.get("username"), body.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}