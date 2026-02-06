package com.example.firstproject.api;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ArticleApiController {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    // 👇 [추가] 현재 로그인한 사용자의 닉네임 가져오기
    @GetMapping("/api/user-info")
    public ResponseEntity<String> getUserInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(null); // 로그인 안 했으면 null 반환
        }
        Member member = memberRepository.findByEmail(principal.getName()).orElse(null);
        if (member != null) {
            return ResponseEntity.ok(member.getNickname()); // 닉네임 반환
        }
        return ResponseEntity.ok(null);
    }

    // --- 아래는 기존 코드들 (그대로 둠) ---

    // 1. 글 목록 조회
    @GetMapping("/api/articles")
    public List<Article> index() {
        return articleRepository.findAll();
    }

    // 2. 글 단건 조회
    @GetMapping("/api/articles/{id}")
    public Article show(@PathVariable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    // 3. 글 쓰기
    @PostMapping("/api/articles")
    public ResponseEntity<Article> create(@RequestBody ArticleForm dto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Article article = dto.toEntity();
        Article newArticle = new Article(null, article.getTitle(), article.getContent(), member.getNickname());

        Article saved = articleRepository.save(newArticle);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    // 4. 글 수정
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id, @RequestBody ArticleForm dto, Principal principal) {
        Article article = dto.toEntity();
        Article target = articleRepository.findById(id).orElse(null);

        if (target == null || id != article.getId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String currentEmail = principal.getName();
        Member currentMember = memberRepository.findByEmail(currentEmail).orElse(null);

        if (target.getAuthor() != null && !target.getAuthor().equals(currentMember.getNickname())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        target.patch(article);
        Article updated = articleRepository.save(target);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    // 5. 글 삭제
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Article> delete(@PathVariable Long id, Principal principal) {
        Article target = articleRepository.findById(id).orElse(null);
        if (target == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        String currentEmail = principal.getName();
        Member currentMember = memberRepository.findByEmail(currentEmail).orElse(null);

        String authorNickname = target.getAuthor();
        String currentNickname = currentMember.getNickname();

        System.out.println("삭제 시도! 글 ID: " + id);
        System.out.println("글 작성자: " + authorNickname);
        System.out.println("요청자: " + currentNickname);

        if (authorNickname == null || !authorNickname.equals(currentNickname)) {
            System.out.println("❌ 삭제 거부!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        System.out.println("✅ 삭제 승인!");
        articleRepository.delete(target);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}