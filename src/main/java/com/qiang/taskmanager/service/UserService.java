package com.qiang.taskmanager.service;

import com.qiang.taskmanager.entity.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    void register(User user);
    User login(String username, String password);
}
