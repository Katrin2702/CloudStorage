package com.example.cloudstorage.service;

import com.example.cloudstorage.dto.AuthenticationDTO;

public interface AuthenticationService {
    String getToken(AuthenticationDTO authenticationDTO);

    void removeToken(String token);
}
