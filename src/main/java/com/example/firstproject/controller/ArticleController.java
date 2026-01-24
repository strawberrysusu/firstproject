package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Slf4j
@Controller

public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form) {
        log.info(form.toString());
        //System.out.println(form.toString());
        Article article = form.toEntity();
        log.info(article.toString());
        //System.out.println(article.toString());
        Article saved = articleRepository.save(article);
        log.info(saved.toString());
        //System.out.println(saved.toString());
        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model) {
        log.info("id = " + id);
        // 1. id 를 조회해 데이터 가져오기
        Article article = articleRepository.findById(id).orElse(null);
        // 2. 모델에 데이터 등록하기
        model.addAttribute("article", article);
        // 3. 뷰 페이지 반환하기 (✅ 빈 따옴표 -> "articles/show" 로 수정!)
        // (아직 show.mustache 안 만들었으면 빈칸("")이어도 에러는 안 나지만, 화면이 안 뜸)
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model) {

        // 1. 모든 데이터 가져오기 (List -> ArrayList로 변경!)
        // (ArticleRepository에서 ArrayList로 바꿨으니까 여기도 맞춰줘야 함)
        ArrayList<Article> articleEntityList = articleRepository.findAll();

        // 2. 모델에 데이터 등록하기 (오타 수정!)
        // (아까는 articlesList라고 썼지? articleEntityList로 통일!)
        model.addAttribute("articleList", articleEntityList);

        // 3. 뷰 페이지 설정하기
        return "articles/index";
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        // 수정할 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);
        // 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity);
        //뷰 페이지 설정하기
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form) {  //매개변수로 DTO 받아오기
        log.info(form.toString());
        // 1. dto 를 엔티티로 변환하기
        Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());
        // 2. 엔티티를 DB 에 저장하기
        // 2-1 DB 에서 기존 데이터를 가져오기
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        //2.2 기존 데이터 값을 갱신하기
        if (target != null) {
            articleRepository.save(articleEntity); //엔티티를 DB 에 저장
        }
        // 3. 수정 결과 페이지로 리다이렉트하기
        return "redirect:/articles/" + articleEntity.getId();
    }

    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr) { // 👈 여기서 시작했으면 안에서 로직을 짜야지!
        log.info("삭제 요청이 들어왔습니다!!");

        // 1. 삭제할 대상 가져오기
        Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());
        // 2. 대상 엔티티 삭제하기
        if (target != null) {
            articleRepository.delete(target); // ✅ 진짜 삭제하는 명령
            rttr.addFlashAttribute("msg" , "삭제되었습니다!");
            // (책 뒷부분에 나오는 "삭제됐습니다" 메시지는 지금 안 중요하니까 패스)
        }

        // 3. 결과 페이지로 리다이렉트 하기 (목록으로 돌아가기)
        return "redirect:/articles";
    }
}
