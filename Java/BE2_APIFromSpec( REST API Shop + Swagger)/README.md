REST API сервис по OpenAPI-спецификации: 
- Разработал REST API магазина (Spring Boot 4 / Java 25, Maven) точно по OpenAPI 3.0-спецификации; документация через springdoc/Swagger UI. 
- Модель данных: продукты, категории, поставщики, клиенты, адреса, изображения, пользователи (JPA/Hibernate, PostgreSQL). - CRUD-эндпоинты `/api/v1/products`, `/suppliers`, `/clients`, `/images` с Bean Validation и централизованной обработкой ошибок (400/404/500); слоистая архитектура Controller → Service → Repository, DTO + мапперы. 
- Безопасность: Spring Security, Basic Auth + BCrypt-хеширование паролей, stateless-сессии.
- Запуск и окружение в Docker Compose (PostgreSQL 15), покрытие тестами (JUnit + spring-security-test).