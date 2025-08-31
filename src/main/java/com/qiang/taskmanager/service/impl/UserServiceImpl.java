package com.qiang.taskmanager.service.impl;

import com.qiang.taskmanager.entity.User;
import com.qiang.taskmanager.mapper.UserMapper;
import com.qiang.taskmanager.service.UserService;
import com.qiang.taskmanager.exception.TaskOperationException;
import com.qiang.taskmanager.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

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
            userMapper.insert(user);
        } catch (Exception e) {
            throw new TaskOperationException("用户注册失败", e);
        }
    }

    @Override
    public User login(String username, String password) throws TaskOperationException {
        try {
            User user = userMapper.findByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new TaskOperationException("用户登录失败，用户名: " + username, e);
        }
    }
}
