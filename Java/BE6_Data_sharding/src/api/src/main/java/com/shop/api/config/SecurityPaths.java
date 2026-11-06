package com.shop.api.config;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class SecurityPaths {
    public static final String[] ENABLED_PATHS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/reset",
            "/api/v1/auth/google",
            "/google-login.html",
            "/oauth-success.html",
            "/oauth2/**",
            "/login",
            "/login/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/error"
    };
    private final AntPathMatcher matcher = new AntPathMatcher();

    public boolean isEnabledPath(String path) {
        for (String pattern : ENABLED_PATHS) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
