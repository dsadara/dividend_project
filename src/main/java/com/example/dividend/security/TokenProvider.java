package com.example.dividend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;   // 1시간 = 1000 밀리세컨드 * 60 * 60
    private static final String KEY_ROLES = "roles";

    @Value("{spring.jwt.secret}")
    private String secretkey;

    /**
     * 토큰 생성(발급)
     *
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        // 사용자의 권한정보를 저장하기 위한 Claim 생성
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();    // 토큰 생성 시간
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)   // 토큰 생성 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretkey)     // 사용할 암호화 알고리즘(HS512), 비밀 키
                .compact();
    }

    // 아래부터는 토큰 생성을 테스트해보기 위한 메소드들

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        // 토큰의 값이 빈 값이면 false
        if (!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        return claims.getExpiration().before(new Date());   // 토큰 만료 시간이 현재시간보다 이전인지(유효한지) 확인
    }

    // 토큰으로부터 claims를 파싱 (토큰 생성을 테스트하기 위함)
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretkey).parseClaimsJws(token).getBody();
        } catch (
                ExpiredJwtException e) {   // 토큰의 만료시간이 지나면 expired Exception이 발생할 수 있음
            return e.getClaims();
        }
    }
}
