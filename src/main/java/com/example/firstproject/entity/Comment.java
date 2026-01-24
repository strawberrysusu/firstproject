package com.example.firstproject.entity;

import com.example.firstproject.dto.CommentDto;
import jakarta.persistence.*; // 👈 이거 없으면 빨간 줄 뜬다. (Alt+Enter 필수)
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id // 👇 이게 있어야 "이게 PK(주민번호)다"라고 인식함!
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 👇 이게 있어야 "게시글(Article)이랑 연결해라"라고 인식함!
    @JoinColumn(name = "article_id") // 테이블에 'article_id'라는 기둥을 세워라!
    private Article article;

    @Column
    private String nickname;

    @Column
    private String body;

    public static Comment createComment(CommentDto dto, Article article) {

        //예외 발생
        if (dto.getId() != null)
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 Id가 없어야 합니다.");

        if(dto.getArticleId() != article.getId())
            throw new IllegalArgumentException("댓글 생성실패 ! 게시글의 id 가 잘못 되었습니다.");
        // 엔티티생성 및 반환

        return new Comment(
                dto.getId(),
                article,
                dto.getNickname(),
                dto.getBody()
        );
    }

    public void patch(CommentDto dto) {
        //예외 발생
        if(this.id !=dto.getId())
            throw new IllegalArgumentException("댓글수정 실패! 잘못된 id 가 입력됐습니다.");

        //객체 갱신
        if(dto.getNickname() != null)
            this.nickname = dto.getNickname();
        if(dto.getBody() != null)
            this.body = dto.getBody();

    }
}