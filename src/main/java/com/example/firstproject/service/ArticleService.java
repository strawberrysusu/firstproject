package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service // 1. "나 서비스(주방장)에요!" 선언
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository; // 보조 요리사(DB 담당) 소환

    // 2. 전체 조회 (GET)
    public List<Article> index() {
        return articleRepository.findAll();
    }

    // 3. 단건 조회 (GET)
    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    // 4. 생성 (POST)
    public Article create(ArticleForm dto) {
        Article article = dto.toEntity();
        // 이미 있는 ID로 들어오면 생성 막기 (책 내용 반영)
        if (article.getId() != null) {
            return null;
        }
        return articleRepository.save(article);
    }

    // 5. 수정 (PATCH)
    public Article update(Long id, ArticleForm dto) {
        // 1. DTO -> 엔티티
        Article article = dto.toEntity();
        log.info("id: {}, article: {}", id, article.toString());

        // 2. 타깃 조회
        Article target = articleRepository.findById(id).orElse(null);

        // 3. 잘못된 요청 처리
        if (target == null || id != article.getId()) {
            log.info("잘못된 요청! id: {}, article: {}", id, article.toString());
            return null; // 컨트롤러가 알아서 400 보냄
        }

        // 4. 업데이트 (patch 메소드 활용)
        target.patch(article);
        Article updated = articleRepository.save(target);
        return updated;
    }

    // 6. 삭제 (DELETE)
    public Article delete(Long id) {
        // 1. 대상 찾기
        Article target = articleRepository.findById(id).orElse(null);

        // 2. 잘못된 요청 처리
        if (target == null) {
            return null;
        }

        // 3. 삭제
        articleRepository.delete(target);
        return target; // 삭제된 녀석 반환
    }
@Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
        //1. dto 묶음을 엔티티 묶음으로 변환하기
        List<Article> articleList = dtos.stream()
                .map(dto -> dto.toEntity())
                .collect(Collectors.toList());
        //2.엔티티 묶음을 db 에 저장하기
        articleList.stream()
                .forEach(article -> articleRepository.save(article));
        //3. 강제 예외 발생시키기
        articleRepository.findById(-1L)
                .orElseThrow(() -> new IllegalArgumentException("결제 실패!!"));
        //4. 결과값 반환하기
        return articleList;
    }
}