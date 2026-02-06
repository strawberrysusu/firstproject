package com.example.firstproject.controller; // 👈 패키지 확인

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequiredArgsConstructor // 👈 이거 있어야 service 자동 주입됨
@Controller
public class ArticleController {


    private final ArticleRepository articleRepository; // 👈 BoardRepository 아님


    private final CommentService commentService;

    // 1. 목록 조회
    @GetMapping("/articles")
    public String index(Model model) {
        // 모든 Article 가져오기
        List<Article> articleList = articleRepository.findAll();

        // 화면에 "articleList"란 이름으로 던짐 (index.mustache 대응)
        model.addAttribute("articleList", articleList);
        return "articles/index";
    }

    // 2. 상세 조회 (여기가 제일 중요!)
    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model) {
        // (1) id로 Article 찾기
        Article article = articleRepository.findById(id).orElse(null);

        // (2) 그 글에 달린 댓글들 찾기
        // ⚠️ 주의: CommentRepository에 findByArticleId 메서드가 있어야 함!
        List<CommentDto> commentDtos = commentService.comments(id);

        // (3) 화면에 전달
        model.addAttribute("article", article);      // show.mustache의 {{#article}} 대응
        model.addAttribute("commentDtos", commentDtos); // 👈 화면으로 댓글 DTO 리스트 보냄

        return "articles/show";
    }

    // 3. 글쓰기 페이지
    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    // 4. 글쓰기 요청
    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form) { // DTO 이름 확인 (ArticleDto일수도 있음)
        log.info(form.toString());

        // DTO -> Entity 변환
        Article article = form.toEntity();

        // 저장
        Article saved = articleRepository.save(article);
        return "redirect:/articles/" + saved.getId();
    }

    // 5. 수정 페이지
    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Article articleEntity = articleRepository.findById(id).orElse(null);
        model.addAttribute("article", articleEntity);
        return "articles/edit";
    }

    // 6. 수정 요청
    @PostMapping("/articles/update")
    public String update(ArticleForm form) {
        Article articleEntity = form.toEntity();
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);

        if (target != null) {
            articleRepository.save(articleEntity);
        }
        return "redirect:/articles/" + articleEntity.getId();
    }

    // 7. 삭제 요청
    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr) {
        Article target = articleRepository.findById(id).orElse(null);
        if (target != null) {
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제됐습니다!");
        }
        return "redirect:/articles";
    }
}