package com.example.firstproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class MemberForm {
    private String email;
    private String password;


//    public Member toEntity() {
//        return new Member(null, email, password);
//    }
}
