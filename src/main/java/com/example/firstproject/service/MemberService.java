package com.example.firstproject.service;

import com.example.firstproject.dto.MemberForm;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 아까 Config에 등록한 암호화 기계

    // 회원가입 메서드
    public Long save(MemberForm dto) {
        return memberRepository.save(Member.builder()
                .email(dto.getEmail())
                // 👇 [핵심] 비밀번호를 그냥 넣지 않고 암호화해서 넣음!
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }
    // ID로 회원 찾기 (토큰 서비스에서 씀)
    public Member findById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}