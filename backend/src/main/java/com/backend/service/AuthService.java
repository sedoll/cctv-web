package com.backend.service;

import com.backend.config.JwtTokenProvider;
import com.backend.domain.member.*;
import com.backend.dto.auth.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationService emailService;

    @Transactional
    public void signup(SignupRequestDto request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        emailService.verifyCode(request.getUsername(), request.getVerificationCode());

        memberRepository.save(Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.ROLE_USER)
                .build());
    }

    public String login(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return jwtTokenProvider.createToken(member.getUsername(), member.getRole());
    }
}