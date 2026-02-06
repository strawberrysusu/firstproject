package com.example.firstproject.api;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Comment;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.CommentRepository; // 👈 추가됨
import com.example.firstproject.repository.MemberRepository;
import com.example.firstproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentApiController {
    private final CommentService commentService;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository; // 👈 검사하려고 추가함!

    // 1. 댓글 조회
    @GetMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long articleId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.comments(articleId));
    }

    // 2. 댓글 생성
    @PostMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long articleId,
                                             @RequestBody CommentDto dto,
                                             Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Member member = memberRepository.findByEmail(principal.getName()).orElse(null);

        CommentDto newDto = new CommentDto(
                dto.getId(),
                dto.getArticleId(),
                member.getNickname(), // 진짜 닉네임 박제
                dto.getBody()
        );

        return ResponseEntity.status(HttpStatus.OK).body(commentService.create(articleId, newDto));
    }

    // 3. 댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id, @RequestBody CommentDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(id, dto));
    }

    // 4. 댓글 삭제 (⭐ 철통 보안 적용)
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long id, Principal principal) {
        // 1. 로그인 안 했으면 컷
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // 2. 지우려는 댓글 찾기
        Comment target = commentRepository.findById(id).orElse(null);
        if (target == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // 3. 본인 확인 (댓글 작성자 vs 로그인한 사람)
        Member currentMember = memberRepository.findByEmail(principal.getName()).orElse(null);
        String currentNickname = currentMember.getNickname();
        String commentNickname = target.getNickname();

        System.out.println("댓글 삭제 시도: " + currentNickname + " vs " + commentNickname);

        // 닉네임 다르면 컷! (옛날 댓글이라 닉네임 없는 경우도 삭제 금지)
        if (commentNickname == null || !commentNickname.equals(currentNickname)) {
            System.out.println("❌ 남의 댓글 삭제 시도 차단됨!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4. 본인 맞으면 삭제 진행
        CommentDto deleted = commentService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}