package com.hp.hospin.global.jwt;

import com.hp.hospin.member.domain.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        return createJwt(member, new Date(now.getTime() + expiredAt.toMillis()));
    }


    public String createJwt(Member member, Date expiry) {
        Date now = new Date();
        return Jwts.builder()
                .claim("identifier", member.getIdentifier())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public boolean isExpired(String token) {
        return parseClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public String getidentifier(String token) {
        return parseClaims(token)
                .get("identifier", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}