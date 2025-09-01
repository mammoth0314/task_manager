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

        // 如果是登录或注册接口，直接放行
        if (request.getRequestURI().contains("/users/login") || request.getRequestURI().contains("/users/register")) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");

        // 检查Authorization头是否存在
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.error(401, "缺少有效的Authorization头信息");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }

        String jwtToken = requestTokenHeader.substring(7);
        String username = null;

        // 提取用户名
        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.error(401, "无效的JWT Token");
            response.getWriter().write(objectMapper.writeValueAsString(result));
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
                    // 在上下文中设置认证信息后，指定当前用户已认证
                    // 这样就能成功通过Spring Security配置
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    Result<Void> result = Result.error(401, "JWT Token已过期或无效");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                Result<Void> result = Result.error(401, "用户不存在或JWT Token无效");
                response.getWriter().write(objectMapper.writeValueAsString(result));
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.error(401, "JWT Token无效");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }

        chain.doFilter(request, response);
    }
}
