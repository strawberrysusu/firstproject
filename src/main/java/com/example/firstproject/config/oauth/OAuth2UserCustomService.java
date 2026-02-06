package com.example.firstproject.config.oauth;

import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 구글에서 유저 정보 가져오기
        OAuth2User user = super.loadUser(userRequest);

        // 2. DB에 저장하거나 업데이트하기
        saveOrUpdate(user);

        return user;
    }

    // 3. 유저가 있으면 업데이트, 없으면 생성
    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        Member member = memberRepository.findByEmail(email)
                .map(entity -> entity.update(name)) // 있으면 이름 업데이트
                .orElse(Member.builder()
                        .email(email)
                        .nickname(name)
                        .build()); // 없으면 새로 생성

        return memberRepository.save(member);
    }
}