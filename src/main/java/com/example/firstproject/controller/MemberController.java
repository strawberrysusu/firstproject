package com.example.firstproject.controller;

import com.example.firstproject.dto.MemberForm;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller

public class MemberController {
    @Autowired
    private MemberRepository memberRepository;
    @GetMapping("members/new")
    public String newArticleForm() {
        return "members/new";
    }
    @PostMapping("/join")
    public String createArticle(MemberForm form){
        log.info(form.toString());
        //System.out.println(form.toString());
        Member member = form.toEntity();
        log.info(member.toString());
        //System.out.println(member.toString());
        Member saved = memberRepository.save(member);
        log.info(saved.toString());
        //System.out.println(saved.toString());
        return "";
    }
}
