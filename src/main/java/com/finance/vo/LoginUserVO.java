package com.finance.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserVO implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private String token; // 如果使用了JWT等，这里可以包含token
}