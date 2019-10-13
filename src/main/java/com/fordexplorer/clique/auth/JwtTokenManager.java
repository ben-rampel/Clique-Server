package com.fordexplorer.clique.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

@Service
public class JwtTokenManager {

    @Autowired
    private UserDetailService userDetailsService;
    private Key serverSecret;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        SecureRandom rand = new SecureRandom();
        byte[] key = new byte[32];
        rand.nextBytes(key);
        serverSecret = Keys.hmacShaKeyFor(key);
    }

    public String createToken(String username) {
        logger.info("Creating token for user {}", username);
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
        logger.info("Loading user info for {}", getUsername(token));
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
