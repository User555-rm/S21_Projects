Nginx(reverse proxy, балансировка, кэш): 
- Настроил Nginx как reverse proxy для 3 инстансов API: балансировка GET-запросов с весами 2:1:1 (round-robin), все записи (POST/PATCH/DELETE) — только на главный инстанс. 
- Read-only реплики 8082/8083 — на роли PostgreSQL `shop_reader` (GRANT SELECT), запись на них заблокирована на уровне БД. 
- HTTPS (TLS 1.2/1.3, self-signed сертификат, редирект 80→443), HTTP/2, gzip-сжатие, прокси-кэширование (proxy_cache) для API-документации и Swagger, кэширование статики, раздача статики, `/status`, проксирование pgAdmin. 
- Автоматизированный чек-лист проверки стенда (PASS/FAIL), кастомный формат access-логов с кодом upstream и статусом кэша.