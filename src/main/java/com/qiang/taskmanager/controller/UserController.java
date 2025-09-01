package com.qiang.taskmanager.controller;

import com.qiang.taskmanager.common.Result;
import com.qiang.taskmanager.entity.User;
import com.qiang.taskmanager.service.UserService;
import com.qiang.taskmanager.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户管理接口")
@SecurityRequirement(name = "Authorization")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;

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
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT Token
            String token = jwtUtil.generateToken(loginUser.getUsername());

            log.info("用户登录成功，用户名: " + loginUser.getUsername(), "token:", token);

            return Result.success(token);
        } catch (Exception e) {
            return Result.error(401, "用户名或密码错误: " + e.getMessage());
        }
    }
}