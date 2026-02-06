package com.example.firstproject.repository;

import com.example.firstproject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

// JpaRepository를 상속받으면 기본 기능(저장, 조회, 삭제)은 자동 탑재됨!
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // [네이티브 쿼리] "SQL을 직접 짜서 검색할게!"
    // :articleId 부분에 파라미터로 들어온 값이 쏙 들어감.
    @Query(value = "SELECT * FROM comment WHERE article_id = :articleId", nativeQuery = true)
    List<Comment> findByArticleId(Long articleId);

    // 특정 닉네임의 모든 댓글 조회 (심심하면 넣어봐)
    List<Comment> findByNickname(String nickname);
}