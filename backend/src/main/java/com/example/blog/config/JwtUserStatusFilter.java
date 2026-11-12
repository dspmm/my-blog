package com.example.blog.config;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.entity.SysUser;
import com.example.blog.repository.SysUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Re-checks account state for signed tokens so disabling or deleting a user
 * takes effect immediately instead of waiting for the JWT to expire.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class JwtUserStatusFilter extends OncePerRequestFilter {

    private final SysUserRepository sysUserRepository;
    private final ObjectMapper objectMapper;

    public JwtUserStatusFilter(SysUserRepository sysUserRepository, ObjectMapper objectMapper) {
        this.sysUserRepository = sysUserRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if ("/api/auth/login".equals(path)
                || "/api/auth/register".equals(path)
                || "/api/auth/send-code".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        Map<String, Object> claims = JwtUtils.parseToken(authorization);
        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Object userIdClaim = claims.get("userId");
        if (!(userIdClaim instanceof Number userIdNumber)) {
            writeUnauthorized(response);
            return;
        }

        SysUser user = sysUserRepository.findById(userIdNumber.longValue()).orElse(null);
        String tokenRole = (String) claims.get("role");
        if (user == null
                || !"0".equals(user.getStatus())
                || tokenRole == null
                || !tokenRole.equalsIgnoreCase(user.getRole())) {
            writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Result.error(401, "登录凭证已失效，请重新登录"));
    }
}
