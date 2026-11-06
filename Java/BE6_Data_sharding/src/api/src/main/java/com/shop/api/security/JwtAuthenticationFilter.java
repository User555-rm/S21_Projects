package com.shop.api.security;

import com.shop.api.config.SecurityPaths;
import com.shop.api.exception.InvalidTokenException;
import com.shop.api.model.User;
import com.shop.api.repository.UserRepository;
import com.shop.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SecurityPaths paths;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserRepository userRepository,
                                   SecurityPaths paths) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.paths = paths;
    }

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
    throws ServletException, IOException {
        String path = request.getRequestURI();
        if(paths.isEnabledPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            response.setStatus(401);
            return;
        }
        String token = header.substring(7);
        String email;
        try {
            email = jwtService.extractEmail(token);
        } catch (InvalidTokenException e) {
            response.setStatus(401);
            return;
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            // токен валиден, юзера нет в БД
            response.setStatus(401);
            return;
        }
        var auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of());
        SecurityContextHolder.getContext()
                .setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
