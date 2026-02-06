package com.example.firstproject.controller;

import com.example.firstproject.dto.MemberForm;
import com.example.firstproject.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {


    private final MemberService memberService;

    // 1. 회원가입 페이지 보여주기
    @GetMapping("/signup")
    public String signup() {
        return "signup"; // signup.mustache 연결
    }

    // 2. 회원가입 처리
    @PostMapping("/user")
    public String signup(MemberForm request) {
        memberService.save(request); // 회원가입 로직 실행
        return "redirect:/login"; // 가입 끝나면 로그인 페이지로 이동
    }

    // 3. 로그인 페이지 보여주기
    @GetMapping("/login")
    public String login() {
        return "login"; // login.mustache 연결
    }

    // 4. 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}