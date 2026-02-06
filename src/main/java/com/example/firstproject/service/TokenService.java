package com.example.firstproject.service;

import com.example.firstproject.config.jwt.TokenProvider;
import com.example.firstproject.entity.Member;
import com.example.firstproject.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    // 리프레시 토큰을 받아서 -> 새로운 액세스 토큰을 발급해주는 메서드
    public String createNewAccessToken(String refreshToken) {
        // 1. 토큰 유효성 검사 (가짜면 에러 냄)
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        // 2. 토큰 주인(userId) 찾기
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();

        // 3. 주인 정보 가져오기
        Member member = memberService.findById(userId);

        // 4. 새 액세스 토큰 발급 (유효기간 2시간)
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}