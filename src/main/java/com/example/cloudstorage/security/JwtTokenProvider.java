package com.example.cloudstorage.security;

import com.example.cloudstorage.model.Role;
import com.example.cloudstorage.repository.TokenBlacklistRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    public final static int BEARER_LENGTH = 7;

    private final UserDetailsService userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    @Value("${jwt.token.secret}")
    private String secret;

    @Autowired
    public JwtTokenProvider(UserDetailsService userDetailsService, TokenBlacklistRepository tokenBlacklistRepository) {
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, List<Role> roles) {
        var claims = Jwts.claims().setSubject(username);
        claims.put("roles", getRoleNames(roles));

        var now = new Date();
        var validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        var userDetails = userDetailsService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserName(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
}

    public String resolveToken(HttpServletRequest req) {
        var bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(BEARER_LENGTH);
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (tokenBlacklistRepository.existsById(token)) throw new JwtAuthenticationException("Blacklisted token");
        try {
            var claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired");
        }
    }

    private List<String> getRoleNames(List<Role> userRoles) {
        var result = new ArrayList<String>();
        userRoles.forEach(role -> result.add(role.getRoleName()));
        return result;
    }
}
