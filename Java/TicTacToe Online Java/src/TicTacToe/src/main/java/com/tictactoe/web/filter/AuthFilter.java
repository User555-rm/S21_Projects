package com.tictactoe.web.filter;

import com.tictactoe.domain.model.JwtAuthentication;
import com.tictactoe.domain.service.JwtProvider;
import com.tictactoe.domain.service.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class AuthFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;

    public AuthFilter(JwtProvider jwtProvider, JwtUtil jwtUtil) {
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws ServletException, IOException{

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String path = httpServletRequest.getRequestURI();
        if (path.equals("/") || path.startsWith("/auth/register") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/auth/refresh-access") ||
                path.startsWith("/auth/refresh-refresh") ||
                path.endsWith(".html") || path.endsWith(".css") ||
                path.endsWith(".js") || path.endsWith(".ico") || path.endsWith(".png")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
            }

        String authHeader = httpServletRequest.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpServletResponse.setStatus(401);
            return;
            }
        String token = authHeader.substring(7);
        boolean checkAuth = jwtProvider.validateAccessToken(token);
        if(checkAuth == false) {
            httpServletResponse.setStatus(401);
            return;
            }
        Claims claims = jwtProvider.getClaims(token);
        JwtAuthentication auth = jwtUtil.buildJwtAuthentication(claims);
        SecurityContextHolder.getContext().setAuthentication(auth);
        httpServletRequest.setAttribute("userId", auth.getUserId());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
