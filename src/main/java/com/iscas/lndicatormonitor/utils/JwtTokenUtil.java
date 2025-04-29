package com.iscas.lndicatormonitor.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "your_secret_key_here";
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5小时

    // 从token中获取用户名
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 从token中获取用户ID
    public Integer getUserIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Integer.class);
    }

    // 从token中获取过期时间
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 从token中获取指定的claim
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 获取token中的所有claims
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // 检查token是否过期
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 生成token
    public String generateToken(String username, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return doGenerateToken(claims, username);
    }

    // 创建token
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 验证token
    public Boolean validateToken(String token) {
        try {
            // 验证token签名
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            // 检查token是否过期
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
