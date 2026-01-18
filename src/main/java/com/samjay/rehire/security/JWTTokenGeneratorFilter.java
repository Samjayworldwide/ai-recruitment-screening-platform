package com.samjay.rehire.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Value("${jwt.key}")
    private String jwtKey;

    @Value("${jwt.header}")
    private String jwtHeader;

    private SecretKey key;

    @PostConstruct
    public void init() {

        this.key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));

    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            String jwt = generateJwt(authentication, key);

            response.setHeader(jwtHeader, jwt);

        }

        filterChain.doFilter(request, response);

    }

    public String getJWT(Authentication authentication) {

        return generateJwt(authentication, key);

    }

    private String generateJwt(Authentication authentication, SecretKey key) {

        return Jwts.builder()
                .issuer("rehire")
                .subject("JWT-Token")
                .claim("email", authentication.getName())
                .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        Set<String> paths = Set.of("/api/v1/signin");

        return paths.contains(request.getServletPath());

    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {

        Set<String> authoritiesSet = new HashSet<>();

        for (GrantedAuthority authority : collection) {

            authoritiesSet.add(authority.getAuthority());

        }

        return String.join(",", authoritiesSet);
    }
}