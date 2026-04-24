package com.campus.donation.config.security;

import com.campus.donation.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取 JWT Token
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                // 解析 Token
                String username = jwtUtil.getUsernameFromToken(token);
                Long userId = jwtUtil.getUserIdFromToken(token);
                Integer userType = jwtUtil.getUserTypeFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 验证 Token
                    if (jwtUtil.validateToken(token, username)) {
                        // 根据 userType 映射权限
                        java.util.List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER_" + userType));
                        
                        if (userType == 1) authorities.add(new SimpleGrantedAuthority("DONOR"));
                        if (userType == 2) authorities.add(new SimpleGrantedAuthority("VOLUNTEER"));
                        if (userType == 3) authorities.add(new SimpleGrantedAuthority("CHARITY_OPERATOR"));
                        if (userType == 4) authorities.add(new SimpleGrantedAuthority("ADMIN"));

                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userId, // principal 设置为 userId
                                null,
                                authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置到 Security 上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("JWT 认证成功: userId={}, username={}, userType={}, authorities={}", 
                                userId, username, userType, authorities);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT 认证失败", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
