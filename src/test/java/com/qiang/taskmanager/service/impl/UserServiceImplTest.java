package com.qiang.taskmanager.service.impl;

import com.qiang.taskmanager.entity.User;
import com.qiang.taskmanager.mapper.UserMapper;
import com.qiang.taskmanager.exception.TaskOperationException;
import com.qiang.taskmanager.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"); // 加密后的密码
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails result = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserNotExists() {
        // Arrange
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));

        assertEquals("用户不存在: nonexistent", exception.getMessage());
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenMapperFails() {
        // Arrange
        when(userMapper.findByUsername("testuser")).thenThrow(new RuntimeException("数据库错误"));

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("testuser"));

        assertEquals("用户不存在: testuser", exception.getMessage());
        assertNotNull(exception.getCause());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userMapper.findById(1L)).thenReturn(testUser);

        // Act
        User result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowTaskNotFoundException_WhenUserNotExists() {
        // Arrange
        when(userMapper.findById(999L)).thenReturn(null);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> userService.findById(999L));

        assertEquals("用户不存在，ID: 999", exception.getMessage());
        verify(userMapper, times(1)).findById(999L);
    }

    @Test
    void findById_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(userMapper.findById(1L)).thenThrow(new RuntimeException("数据库错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> userService.findById(1L));

        assertEquals("查询用户失败，ID: 1", exception.getMessage());
        assertNotNull(exception.getCause());
        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // Act
        User result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_ShouldThrowTaskNotFoundException_WhenUserNotExists() {
        // Arrange
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> userService.findByUsername("nonexistent"));

        assertEquals("用户不存在，用户名: nonexistent", exception.getMessage());
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }

    @Test
    void findByUsername_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(userMapper.findByUsername("testuser")).thenThrow(new RuntimeException("数据库错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> userService.findByUsername("testuser"));

        assertEquals("根据用户名查询用户失败，用户名: testuser", exception.getMessage());
        assertNotNull(exception.getCause());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void register_ShouldRegisterUserSuccessfully() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plainpassword"); // 明文密码

        doNothing().when(userMapper).insert(any(User.class));

        // Act
        userService.register(newUser);

        // Assert
        verify(userMapper, times(1)).insert(any(User.class));
        // 验证密码被加密（不是原来的明文密码）
        assertNotEquals("plainpassword", newUser.getPassword());
        assertTrue(newUser.getPassword().startsWith("$2a$")); // BCrypt加密格式
    }

    @Test
    void register_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");

        doThrow(new RuntimeException("插入错误")).when(userMapper).insert(any(User.class));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> userService.register(newUser));

        assertEquals("用户注册失败", exception.getMessage());
        assertNotNull(exception.getCause());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreCorrect() {
        // Arrange
        String rawPassword = "correctpassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setId(1L);
        userWithEncodedPassword.setUsername("testuser");
        userWithEncodedPassword.setPassword(encodedPassword);

        when(userMapper.findByUsername("testuser")).thenReturn(userWithEncodedPassword);

        // Act
        User result = userService.login("testuser", rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void login_ShouldReturnNull_WhenPasswordIsIncorrect() {
        // Arrange
        String encodedPassword = new BCryptPasswordEncoder().encode("correctpassword");

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setId(1L);
        userWithEncodedPassword.setUsername("testuser");
        userWithEncodedPassword.setPassword(encodedPassword);

        when(userMapper.findByUsername("testuser")).thenReturn(userWithEncodedPassword);

        // Act
        User result = userService.login("testuser", "wrongpassword");

        // Assert
        assertNull(result);
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void login_ShouldReturnNull_WhenUserNotExists() {
        // Arrange
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // Act
        User result = userService.login("nonexistent", "password");

        // Assert
        assertNull(result);
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }

    @Test
    void login_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(userMapper.findByUsername("testuser")).thenThrow(new RuntimeException("数据库错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> userService.login("testuser", "password"));

        assertEquals("用户登录失败，用户名: testuser", exception.getMessage());
        assertNotNull(exception.getCause());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void login_ShouldWorkWithDifferentBCryptVersions() {
        // Arrange - 测试不同版本的BCrypt加密密码
        String[] testPasswords = {
                "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", // 模拟的加密密码
                new BCryptPasswordEncoder().encode("testpassword") // 实际加密的密码
        };

        for (String encodedPassword : testPasswords) {
            User testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setPassword(encodedPassword);

            when(userMapper.findByUsername("testuser")).thenReturn(testUser);

            // Act & Assert - 对于模拟的加密密码，应该返回null（密码不匹配）
            // 对于实际加密的密码，应该能够正确验证
            User result = userService.login("testuser", "testpassword");
            if (encodedPassword.startsWith("$2a$10$abcdef")) {
                assertNull(result); // 模拟的加密密码不会匹配
            } else {
                assertNotNull(result); // 实际加密的密码应该匹配
            }
        }
    }
}