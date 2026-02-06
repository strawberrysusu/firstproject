package com.example.firstproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {
    private String refreshToken; // 리프레시 토큰 주세요
}