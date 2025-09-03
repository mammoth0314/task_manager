package com.qiang.taskmanager.controller;

import com.qiang.taskmanager.common.Result;
import com.qiang.taskmanager.entity.User;
import com.qiang.taskmanager.service.UserService;
import com.qiang.taskmanager.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        SecurityContextHolder.clearContext();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
    }

    @Test
    void testRegister_Success() throws Exception {
        // 模拟行为
        doNothing().when(userService).register(any(User.class));

        String userJson = """
        {
            "username": "testuser",
            "password": "password123",
            "email": "test@example.com"
        }
        """;

        // 执行测试 - 根据你的Result类实际返回调整断言
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("注册成功")); // 根据实际返回调整

        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    void testLogin_Success() throws Exception {
        // 先查看你的Controller实际如何调用authenticationManager
        // 如果确实需要模拟，再取消注释下面的代码

        /*
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        */

        when(jwtUtil.generateToken("testuser")).thenReturn("mock-jwt-token");

        String loginJson = """
        {
            "username": "testuser",
            "password": "password123"
        }
        """;

        // 执行测试
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(result -> {
                    // 先查看实际响应内容
                    System.out.println("Login response: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("mock-jwt-token"));

        // 根据实际情况调整验证
        // verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testLogin_Failure_BadCredentials() throws Exception {
        // 模拟认证失败
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        String loginJson = """
        {
            "username": "wronguser",
            "password": "wrongpassword"
        }
        """;

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(result -> {
                    System.out.println("Login failure response: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误: Bad credentials"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    // 调试方法：查看注册响应的实际内容
    @Test
    void debugRegisterResponse() throws Exception {
        doNothing().when(userService).register(any(User.class));

        String userJson = """
        {
            "username": "testuser",
            "password": "password123",
            "email": "test@example.com"
        }
        """;

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(result -> {
                    System.out.println("注册响应状态: " + result.getResponse().getStatus());
                    System.out.println("注册响应内容: " + result.getResponse().getContentAsString());
                });
    }

    // 调试方法：查看登录响应的实际内容
    @Test
    void debugLoginResponse() throws Exception {
        String loginJson = """
        {
            "username": "testuser",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(result -> {
                    System.out.println("登录响应状态: " + result.getResponse().getStatus());
                    System.out.println("登录响应内容: " + result.getResponse().getContentAsString());
                });
    }
}