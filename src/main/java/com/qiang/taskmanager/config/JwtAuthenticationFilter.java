package com.qiang.taskmanager.config;

import com.qiang.taskmanager.common.Result;
import com.qiang.taskmanager.service.UserService;
import com.qiang.taskmanager.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 过滤请求并验证JWT token
     * @param request HTTP请求
     * @param response HTTP响应
     * @param chain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 首先处理OPTIONS预检请求 - 必须放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        // 如果是登录或注册接口，直接放行
        if (request.getRequestURI().contains("/users/login")
                || request.getRequestURI().contains("/users/register")
                || request.getRequestURI().contains("/doc.html")
                || request.getRequestURI().contains("/v3/api-docs")
                || request.getRequestURI().contains("/webjars/")
                || request.getRequestURI().contains("/swagger-resources")
                || request.getRequestURI().contains("/swagger-ui")
                || request.getRequestURI().contains("/api-docs")
                || request.getRequestURI().contains("/swagger")) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;

        // 检查Authorization头是否存在
        if (requestTokenHeader == null) {
            sendErrorResponse(response, 401, "缺少Authorization头信息");
            return;
        }

        // 支持两种格式：带Bearer前缀和不带Bearer前缀
        if (requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); // 去掉"Bearer "前缀
        } else {
            jwtToken = requestTokenHeader; // 直接使用整个字符串作为token
        }

        // 检查token是否为空
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            sendErrorResponse(response, 401, "Token不能为空");
            return;
        }

        String username = null;

        // 提取用户名
        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
            sendErrorResponse(response, 401, "无效的JWT Token");
            return;
        }

        // 验证token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userService.findByUsername(username);

                // 如果token有效，则手动配置Spring Security认证信息
                if (jwtUtil.validateToken(jwtToken, username)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    sendErrorResponse(response, 401, "JWT Token已过期或无效");
                    return;
                }
            } catch (Exception e) {
                sendErrorResponse(response, 401, "用户不存在或JWT Token无效");
                return;
            }
        } else {
            sendErrorResponse(response, 401, "JWT Token无效");
            return;
        }

        chain.doFilter(request, response);
    }

    // 添加一个辅助方法来发送错误响应
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}