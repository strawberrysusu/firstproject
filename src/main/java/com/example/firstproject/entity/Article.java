package com.example.firstproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter

public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 👈 이거 하나면 끝난다.
    private Long id;
    @Column
    private String title;
    @Column
    private String content;
// Article.java 파일 안쪽 (클래스 끝나기 전)

    public void patch(Article article) {
        if (article.title != null) {
            this.title = article.title;
        }
        if (article.content != null) {
            this.content = article.content;
        }
    }
}