package com.example.bigevent.service;

import com.example.bigevent.pojo.User;

public interface UserService {
    User findByUserName(String username);

    void register(String username, String password);
}
