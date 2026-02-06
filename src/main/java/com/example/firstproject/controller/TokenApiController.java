package com.example.firstproject.controller;

import com.example.firstproject.config.jwt.TokenProvider;
import com.example.firstproject.dto.CreateAccessTokenRequest;
import com.example.firstproject.dto.CreateAccessTokenResponse;
import com.example.firstproject.repository.RefreshTokenRepository;
import com.example.firstproject.service.MemberService;
import com.example.firstproject.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager; // 로그인 인증 담당자

    // 1. 로그인 API (이메일/비번 받아서 -> 토큰 2개 줌)
//    @PostMapping("/api/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//
//        // (1) ID/PW가 맞는지 검사 (틀리면 여기서 알아서 에러 남)
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // (2) 멤버 정보 가져오기
//        Member member = (Member) authentication.getPrincipal(); // 인증된 사용자 정보
//
//        // (3) 액세스 토큰 만들기 (2시간짜리)
//        String accessToken = tokenProvider.generateToken(member, Duration.ofHours(2));
//
//        // (4) 리프레시 토큰 만들기 (2주짜리)
//        String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));
//
//        // (5) 리프레시 토큰을 DB에 저장 (나중에 갱신할 때 확인용)
//        // 이미 있으면 업데이트, 없으면 새로 저장
//        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(member.getId())
//                .map(entity -> entity.update(refreshToken))
//                .orElse(new RefreshToken(member.getId(), refreshToken));
//
//        refreshTokenRepository.save(refreshTokenEntity);
//
//        // (6) 토큰 2개 담아서 응답
//        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
//    }

    // 2. 토큰 갱신 API (기존 코드 유지)
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
            @RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}