package com.example.firstproject.api;

import com.example.firstproject.config.jwt.TokenProvider;
import com.example.firstproject.dto.LoginRequest;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비번 맞나 확인용

    // 일반 로그인 API
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. 이메일로 회원 찾기
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 확인 (입력한 비번 vs DB에 있는 암호화된 비번)
        if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 틀렸습니다.");
        }

        // 3. 토큰 생성 (2시간짜리)
        String token = tokenProvider.generateToken(member, Duration.ofHours(2));

        // 4. 토큰 반환 (클라이언트에게 줌)
        return ResponseEntity.ok(Map.of("token", token));
    }
}