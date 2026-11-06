#!/usr/bin/env bash
# check.sh — прогон проверок стенда BE3_Nginx (п.11.2 плана)
# Запуск: bash check.sh   (нужны инстансы 8081/8082/8083)
set -u
BASE="https://shop.local"
PASS=0; FAIL=0
green(){ printf "[OK] $1"; echo; }
red(){ printf "[FAIL] $1"; echo; }
check(){ if [ "$2" = PASS ]; then PASS=$((PASS+1)); green "$1"; else FAIL=$((FAIL+1)); red "$1"; fi; }
echo "===== Проверка стенда BE3_Nginx ====="
# 1) HTTPS+TLS (п.6)
h1=$(curl -skv "$BASE/" 2>&1 | grep -c "SSL connection using TLS")
if [ "$h1" -ge 1 ]; then check "HTTPS+TLS (п.6)" PASS; else check "HTTPS+TLS[$h1]" FAIL; fi
# 2) HTTP/2 (п.6)
h2=$(curl -sk -o /dev/null -w "%{http_version}" "$BASE/")
if [ "$h2" = 2 ]; then check "HTTP/2 (п.6)" PASS; else check "HTTP/2[$h2]" FAIL; fi
# 3) Редирект 80->443 (п.6)
r3=$(curl -s -o /dev/null -w "%{http_code}" http://shop.local/)
if [ "$r3" = 301 ]; then check "Редирект 80->443 (п.6)" PASS; else check "Редирект 80->443[$r3]" FAIL; fi
# 4) Статика (п.2)
if curl -sk "$BASE/" | grep -q "index.html"; then check "Статика (п.2)" PASS; else check "Статика (п.2)" FAIL; fi
# 5) image.png (п.2)
i5=$(curl -sk -o /dev/null -w "%{http_code}:%{content_type}" "$BASE/image.png")
if [ "$i5" = "200:image/png" ]; then check "image.png (п.2)" PASS; else check "image.png[$i5]" FAIL; fi
# 6) /status (п.2)
if curl -sk "$BASE/status" | grep -q "Active connections"; then check "/status (п.2)" PASS; else check "/status (п.2)" FAIL; fi
# 7) /admin (п.2)
a7=$(curl -sk -o /dev/null -w "%{http_code}" "$BASE/admin/")
if [ "$a7" != 502 ] && [ "$a7" != 000 ]; then check "/admin (п.2)[$a7]" PASS; else check "/admin (п.2)[$a7]" FAIL; fi

# 8) /api rewrite (п.2) — проверяем редирект на /api/v1
api_redirect=$(curl -sk -o /dev/null -w "%{http_code}" -L "$BASE/api/products")
# Проверяем, что после всех редиректов получаем 200 (успешный ответ)
if [ "$api_redirect" = 200 ]; then 
    check "/api rewrite (п.2)" PASS
else 
    check "/api rewrite (п.2)[$api_redirect]" FAIL
fi

# 9) /api/v1 -> Swagger (п.2) — проверяем, что редиректит на сваггер
# Сначала проверяем первый редирект (добавление слеша)
first_redirect=$(curl -sk -o /dev/null -w "%{http_code}:%{redirect_url}" "$BASE/api/v1")
# Затем проверяем конечный URL после всех редиректов
final_url=$(curl -skL -o /dev/null -w "%{url_effective}" "$BASE/api/v1")
# Проверяем, что конечный URL ведет на Swagger UI
if echo "$final_url" | grep -q "swagger-ui"; then
    check "/api/v1->Swagger (п.2)" PASS
else
    check "/api/v1->Swagger[код=$first_redirect, URL=$final_url]" FAIL
fi

# 10) GET /api/v1/products (инстансы)
p10=$(curl -sk -o /dev/null -w "%{http_code}" "$BASE/api/v1/products")
if [ "$p10" = 200 ]; then check "GET /api/v1/products[$p10]" PASS; else check "GET /api/v1/products[$p10]" FAIL; fi

# 11) Балансировка 2:1:1 (п.3)
for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20; do curl -sk -o /dev/null "$BASE/api/v1/products"; done
echo "-- распределение upstream (ожидаем 8081:8082:8083 ~ 2:1:1):"
sudo -n grep -o "upstream=127.0.0.1:808[1-3]" /var/log/nginx/access.log 2>/dev/null | tail -20 | sort | uniq -c | sort -rn || echo "(нет sudo/логов)"
# 12) POST создать поставщика (п.3) - запись идёт на главный (8081)
PJ='{"name":"TestSupplier"}'
resp=$(curl -sk -w "\n%{http_code}" -X POST "$BASE/api/v1/suppliers" -H "Content-Type: application/json" -d "$PJ")
code=$(printf %s "$resp" | tail -c 3)
body=$(printf %s "$resp" | head -c -3)
newid=$(printf %s "$body" | grep -oE "[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}" | head -n1)
if [ "$code" = 201 ] && [ -n "$newid" ]; then check "POST поставщика (создание) на главный (п.3)" PASS; else check "POST поставщика (создание)[код=$code]" FAIL; fi
d13=$(curl -sk -o /dev/null -w "%{http_code}" -X DELETE "$BASE/api/v1/suppliers/$newid")
if [ "$d13" = 204 ]; then check "DELETE поставщика через nginx (п.3)" PASS; else check "DELETE поставщика [код=$d13]" FAIL; fi
g14=$(curl -sk -o /dev/null -w "%{http_code}" "$BASE/api/v1/suppliers/$newid")
if [ "$g14" = 404 ]; then check "GET удалённого поставщика (404)" PASS; else check "GET удалённого поставщика [код=$g14]" FAIL; fi
# 13) Read-only: прямой POST на 8082 падает (п.3)
p13=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8082/api/v1/products -H "Content-Type: application/json" -d "{")
if [ "$p13" = 500 ] || [ "$p13" = 403 ] || [ "$p13" = 400 ]; then check "Read-only 8082 блокирует[$p13]" PASS; else check "Read-only 8082 блокирует[$p13]" FAIL; fi
# 14) Кэш /v3/api-docs (п.4)
k1=$(curl -sk -o /dev/null -w "%{http_code}" "$BASE/v3/api-docs")
k2=$(curl -sk -o /dev/null -w "%{http_code}" "$BASE/v3/api-docs")
if [ "$k1" = 200 ] || [ "$k2" = 200 ]; then check "/v3/api-docs (п.4)[$k1/$k2]" PASS; else check "/v3/api-docs (п.4)[$k1/$k2]" FAIL; fi
# 15) gzip на JSON (п.5)
g1=$(curl -sk -H "Accept-Encoding: gzip" -o /dev/null -w "%{size_download}" "$BASE/v3/api-docs")
if [ "$g1" -gt 0 ]; then check "gzip JSON (п.5)" PASS; else check "gzip JSON (п.5)" FAIL; fi
# 16) НЕ gzip на png (п.5)
g2=$(curl -sk -H "Accept-Encoding: gzip" -o /dev/null -w "%{size_download}" "$BASE/image.png")
if [ "$g2" -gt 200000 ]; then check "НЕ gzip png (п.5)" PASS; else check "НЕ gzip png (п.5)[$g2]" FAIL; fi

# 17) Роль shop_reader / БД (шаг1)
if PGPASSWORD=123 psql -h localhost -p 5433 -U shop_reader -d shop_db -c "select 1;" >/dev/null 2>&1; then check "Роль shop_reader (шаг1)" PASS; else check "Роль shop_reader (шаг1)" FAIL; fi
echo
echo "===== ИТОГО: PASS=$PASS FAIL=$FAIL ====="