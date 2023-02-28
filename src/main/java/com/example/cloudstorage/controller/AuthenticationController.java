package com.example.cloudstorage.controller;

import com.example.cloudstorage.dto.AuthenticationDTO;
import com.example.cloudstorage.dto.TokenDTO;
import com.example.cloudstorage.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public TokenDTO login(@RequestBody AuthenticationDTO authentication) {
        var token = new TokenDTO();
        token.setValue(authenticationService.getToken(authentication));
        return token;
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) {
        authenticationService.removeToken(token);
        return new ResponseEntity(HttpStatus.OK);
    }
}
