package com.example.cloudstorage.service.Impl;

import com.example.cloudstorage.dto.AuthenticationDTO;
import com.example.cloudstorage.model.TokenBlacklist;
import com.example.cloudstorage.repository.TokenBlacklistRepository;
import com.example.cloudstorage.security.JwtTokenProvider;
import com.example.cloudstorage.service.AuthenticationService;
import com.example.cloudstorage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public String getToken(AuthenticationDTO authentication) {
        try {
            var login = authentication.getLogin();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, authentication.getPassword()));
            var user = userService.findByLogin(login);
            return jwtTokenProvider.createToken(login, user.getRoles());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("The username or password you entered is incorrect");
        }
    }

    @Override
    public void removeToken(String token) {
        var forbiddenToken = new TokenBlacklist();
        forbiddenToken.setToken(token);
        tokenBlacklistRepository.save(forbiddenToken);
    }
}