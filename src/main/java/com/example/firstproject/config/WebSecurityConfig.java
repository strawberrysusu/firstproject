package com.example.firstproject.config;

import com.example.firstproject.config.jwt.TokenAuthenticationFilter;
import com.example.firstproject.config.jwt.TokenProvider;
import com.example.firstproject.config.oauth.OAuth2SuccessHandler;
import com.example.firstproject.config.oauth.OAuth2UserCustomService;
import com.example.firstproject.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;
    private final TokenProvider tokenProvider;
    // 👇 [추가] OAuth2 관련 부품 2개
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/h2-console/**", "/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 👇 [기존] 토큰 필터 추가
                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                // 👇 [기존] URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token", "/api/login").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                // 👇 [신규] OAuth2 로그인 설정 (여기가 핵심!)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 로그인 페이지 경로
                        // 1. 로그인 성공 후 사용자 정보 가져오는 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
                        // 2. 로그인 성공 후 처리할 핸들러 (토큰 발급)
                        .successHandler(oAuth2SuccessHandler)
                )
                .build();
    }
    // 3. 인증 관리자 (똑같음)
    // 👇 [수정] 인증 관리자 설정 (최신 버전은 이렇게 짧게 씁니다!)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 4. 암호화 기계 (똑같음)
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}