package com.fordexplorer.clique.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;

@Service
public class JwtTokenManager {

    @Autowired
    private UserDetailService userDetailsService;
    private Key serverSecret;

    @PostConstruct
    public void init() {
        serverSecret = Keys.hmacShaKeyFor("password".getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", Arrays.asList("user"));

        Date now = new Date();
        Date valid = new Date(Long.MAX_VALUE);

        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(valid).signWith(serverSecret, SignatureAlgorithm.HS256).compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(serverSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(serverSecret).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Expired or invalid JWT token");
        }
    }

}
