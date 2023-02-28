package com.example.cloudstorage.service;

import com.example.cloudstorage.model.User;

public interface UserService {
    User findByLogin(String login);

    User findById(Long id);
}
