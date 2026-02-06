package com.example.firstproject.dto;

import com.example.firstproject.entity.Article;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ArticleForm {
    private Long id;
    private String title;
    private String content;
    // 👇 [추가] 폼 데이터나 DTO에서 author를 다룰 수 있게 필드 추가
    // (화면에서 안 보내더라도, 나중에 확장성을 위해 넣어두면 좋음)
    private String author;

    // DTO -> Entity 변환 메서드
    public Article toEntity() {
        // 생성자 순서: id, title, content, author
        // 폼에서 author가 안 넘어오면 null로 들어감 -> 컨트롤러에서 토큰 정보로 채워줄 거임
        return new Article(id, title, content, author);
    }
}