package com.shop.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/api/v1")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}

// Полный путь: https://shop.local/api/v1
/*
Как работает: пользователь открывает https://shop.local/api/v1 → nginx проксирует
на бэкенд → контроллер отвечает 302 Location: /swagger-ui/index.html → браузер
запрашивает /swagger-ui/index.html → nginx отдаёт это через отдельный location на 8081,
и springdoc показывает UI. Заодно /swagger-ui/... и /v3/api-docs — это GET-запросы
вне /api, поэтому они обслуживаются через прокси-кэш (п. 4).
 */
