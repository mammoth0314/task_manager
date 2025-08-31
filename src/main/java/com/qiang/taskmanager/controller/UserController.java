package com.qiang.taskmanager.controller;

import com.qiang.taskmanager.common.Result;
import com.qiang.taskmanager.entity.User;
import com.qiang.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    // 用户注册
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result register(@RequestBody User user) {
        log.info("用户注册，用户名：{}", user.getUsername());
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(500, "注册失败：" + e.getMessage());
        }
    }

    // 用户登录
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result login(@RequestBody User loginUser) {
        log.info("用户登录，用户名：{}", loginUser.getUsername());
        try {
            User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
            if (user != null) {
                return Result.success(user.getUsername());
            } else {
                return Result.error(401, "用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }
}
