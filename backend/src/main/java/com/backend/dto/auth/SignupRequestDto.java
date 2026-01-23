package com.backend.dto.auth; // [변경]
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequestDto {
    private String username;
    private String password;
    private String nickname;
    private String verificationCode;
}