package com.qiang.taskmanager.service;

import com.qiang.taskmanager.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findById(Long id);
    User findByUsername(String username);
    void register(User user);
    Object login(String username, String password);
}