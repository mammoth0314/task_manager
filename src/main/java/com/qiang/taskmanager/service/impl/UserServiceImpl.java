package com.qiang.taskmanager.service.impl;

import com.qiang.taskmanager.entity.User;
import com.qiang.taskmanager.mapper.UserMapper;
import com.qiang.taskmanager.service.UserService;
import com.qiang.taskmanager.exception.TaskOperationException;
import com.qiang.taskmanager.exception.TaskNotFoundException;
import com.qiang.taskmanager.util.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userMapper.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            return user;
        } catch (Exception e) {
            throw new UsernameNotFoundException("用户不存在: " + username, e);
        }
    }

    @Override
    public User findById(Long id) throws TaskOperationException {
        try {
            User user = userMapper.findById(id);
            if (user == null) {
                throw new TaskNotFoundException("用户不存在，ID: " + id);
            }
            return user;
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskOperationException("查询用户失败，ID: " + id, e);
        }
    }

    @Override
    public User findByUsername(String username) throws TaskOperationException {
        try {
            User user = userMapper.findByUsername(username);
            if (user == null) {
                throw new TaskNotFoundException("用户不存在，用户名: " + username);
            }
            return user;
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskOperationException("根据用户名查询用户失败，用户名: " + username, e);
        }
    }

    @Override
    public void register(User user) throws TaskOperationException {
        try {
            // 对密码进行BCrypt加密
            String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            user.setPassword(encodedPassword);
            userMapper.insert(user);
        } catch (Exception e) {
            throw new TaskOperationException("用户注册失败", e);
        }
    }

    @Override
    public User login(String username, String password) throws TaskOperationException {
        try {
            User user = userMapper.findByUsername(username);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new TaskOperationException("用户登录失败，用户名: " + username, e);
        }
    }
}