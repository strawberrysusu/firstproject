package com.example.firstproject.config.oauth;

import com.example.firstproject.config.jwt.TokenProvider;
import com.example.firstproject.entity.Member;
import com.example.firstproject.entity.RefreshToken;
import com.example.firstproject.repository.MemberRepository;
import com.example.firstproject.repository.RefreshTokenRepository;
import com.example.firstproject.util.CookieUtil; // 👈 이건 잠시 후에 만들 거임 (빨간줄 무시)
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws java.io.IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member member = memberRepository.findByEmail((String) oAuth2User.getAttributes().get("email"))
                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));

        // 1. 리프레시 토큰 생성 -> 저장 -> 쿠키에 굽기
        String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));
        saveRefreshToken(member.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 2. 액세스 토큰 생성 -> URL 쿼리 파라미터에 붙이기
        String accessToken = tokenProvider.generateToken(member, Duration.ofHours(2));
        String targetUrl = getTargetUrl(accessToken);

        // 3. 페이지 이동 (토큰 달고 감)
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // (내부용) 리프레시 토큰 DB 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // (내부용) 쿠키에 리프레시 토큰 저장 (나중에 갱신할 때 씀)
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) Duration.ofDays(14).toSeconds();
        CookieUtil.deleteCookie(request, response, "refresh_token");
        CookieUtil.addCookie(response, "refresh_token", refreshToken, cookieMaxAge);
    }

    // (내부용) 이동할 주소 만들기 (/articles?token=... 형태)
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString("/articles")
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}