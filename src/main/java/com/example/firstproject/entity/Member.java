package com.example.firstproject.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "members") // 테이블 이름 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member implements UserDetails { // 👈 중요! "나 이제 시큐리티용 유저야!" 선언

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Builder
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
    // 👇 [추가 2] 닉네임 변경하는 메서드 (구글 이름 바뀌면 우리도 바꾸려고)
    public Member update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    // 👇 아래는 UserDetails 인터페이스가 시키는 필수 메서드들 (시큐리티가 로그인할 때 씀)

    @Override // 권한 반환 (우리는 무조건 "user" 권한을 준다고 가정)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override // 사용자의 패스워드 반환
    public String getPassword() {
        return password;
    }

    @Override // 사용자의 ID(이메일) 반환
    public String getUsername() {
        return email; // 시큐리티는 id를 username이라고 부름 (우린 email을 id로 씀)
    }

    @Override // 계정 만료 안 됐니? (true: 안 만료됨)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 잠긴 거 아니니? (true: 안 잠김)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 비번 만료 안 됐니? (true: 안 만료됨)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 활성화 돼 있니? (true: 활성화)
    public boolean isEnabled() {
        return true;
    }
}