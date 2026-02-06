package com.example.firstproject.service;

import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService { // 👈 시큐리티 표준 인터페이스 구현

    private final MemberRepository memberRepository;

    @Override
    public Member loadUserByUsername(String email) {
        // DB에서 이메일로 사람 찾고, 없으면 에러 냄
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}