// src/main/java/com/finance/util/JwtUtil.java
package com.finance.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类：生成、解析、验证 Token
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:finance_helper_jwt_secret_2024}")
    private String secret;

    @Value("${jwt.expire:3600000}") // 1小时 (单位: ms)
    private long expire;

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(userId.toString())                 // 主题 = 用户ID
                .claim("username", username)                   // 自定义声明
                .setIssuedAt(new Date())                       // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expire)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, secret)    // 签名算法
                .compact();
    }

    /**
     * 从 Token 中提取 Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaimsFromToken(token).getSubject());
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
