package com.tictactoe.config;

import com.tictactoe.web.filter.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {
private final AuthFilter authFilter;

public SecurityConfig (AuthFilter authFilter) {
    this.authFilter = authFilter;
}


/*.csrf(csrf -> csrf.disable())
Отключает защиту от CSRF (Cross-Site Request Forgery).
Если бэкенд работает как REST API (без сохранения сессий в куках) и
используется JWT-токенами, CSRF-атаки становятся неактуальными. Если не отключить,
POST/PUT запросы без специального токена будут блокироваться с ошибкой 403.

.sessionManagement(session -> session.sessionCreationPolicy(
        SessionCreationPolicy.STATELESS))
Указывает Spring НЕ создавать и НЕ использовать HTTP-сессии.
Политика STATELESS означает, что каждый запрос обрабатывается независимо (сервер
не хранит состояние клиента). Это стандарт для JWT-аутентификации, так как вся
информация о пользователе передаётся в самом токене.

.anonymous(anonymous -> anonymous.disable())
По умолчанию Spring Security создаёт AnonymousAuthenticationToken
для неавторизованных запросов. Это может мешать: getJwtAuthentication()
в сервисе будет получать анонимный токен вместо null.

Логика работы:
Приходят запросы на /auth/* — пропускаем без проверки.
На все остальные запросы навешивается ваш authFilter, который проверяет JWT.
Сессии не используются, CSRF отключён.
Если токен валидный — доступ разрешён, если нет — вернётся 401 Unauthorized. */
@Bean
public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
httpSecurity
.csrf(csrf -> csrf.disable())
.sessionManagement(session -> session.sessionCreationPolicy(
        SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/register",
                    "/auth/login", "/auth/refresh-access",
                    "/auth/refresh-refresh",
                    "/", "/index.html", "/style.css", "/script.js", "/error")
                    .permitAll()
                    .anyRequest().authenticated())
    .addFilterBefore(authFilter, BasicAuthenticationFilter.class);
    return httpSecurity.build();
    }
}
