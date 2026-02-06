package com.example.firstproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")  // 그냥 주소만 치고 들어왔을 때
    public String index() {
        // "/articles"로 강제 이동! 
        // (로그인 안 된 상태면 스프링 시큐리티가 알아서 로그인 창으로 보냄)
        return "redirect:/articles";
    }
}