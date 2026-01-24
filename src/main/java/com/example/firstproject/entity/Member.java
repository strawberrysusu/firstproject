package com.example.firstproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@ToString
@Entity
public class Member { // ✅ 1. 클래스 이름 변경 (Article -> Member)

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String email;    // ✅ 2. 필드 변경 (title -> email)

    @Column
    private String password; // ✅ 3. 필드 변경 (content -> password)

    // ✅ 4. 기본 생성자 변경 (Article -> Member)
    public Member() {
    }


}