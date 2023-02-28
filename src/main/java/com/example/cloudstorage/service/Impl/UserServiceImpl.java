package com.example.cloudstorage.service.Impl;

import com.example.cloudstorage.exception.CloudServiceNotFoundException;
import com.example.cloudstorage.model.User;
import com.example.cloudstorage.repository.UserRepository;
import com.example.cloudstorage.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final static String USER_NOT_FOUND_LOGIN = "User with login=[%s] not found";
    private final static String USER_NOT_FOUND_ID = "User with id=[%s] not found";

    private final UserRepository userRepository;

    @Override
    public User findByLogin(String login) {
        var user = userRepository.findByUsername(login)
                .orElseThrow(() -> new CloudServiceNotFoundException(format(USER_NOT_FOUND_LOGIN, login)));
        log.info("User: {} found by login: {}", user, login);
        return user;
    }

    @Override
    public User findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new CloudServiceNotFoundException(format(USER_NOT_FOUND_ID, id)));
        log.info("User: {} found by id: {}", user, id);
        return user;
    }

}