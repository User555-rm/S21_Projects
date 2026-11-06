-- ============================================================
-- Роль shop_reader — инстансы 8082/8083 (только чтение).
-- Выполнять от имени владельца БД (postgres) в базе shop_db.
-- Скрипт идемпотентен: повторный запуск не создаёт дубль роли.
-- ============================================================

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'shop_reader') THEN
        CREATE ROLE shop_reader WITH LOGIN PASSWORD '123';
    END IF;
END
$$;

GRANT CONNECT ON DATABASE shop_db TO shop_reader;
GRANT USAGE   ON SCHEMA public TO shop_reader;
GRANT SELECT  ON ALL TABLES  IN SCHEMA public TO shop_reader;
GRANT SELECT  ON ALL SEQUENCES IN SCHEMA public TO shop_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES    TO shop_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON SEQUENCES TO shop_reader;

-- После миграции images.image на bytea специальные права на large objects
-- не требуются (достаточно GRANT SELECT на таблицу images выше).
-- Если миграцию откатывали обратно на oid — раскомментируйте:
-- GRANT SELECT ON pg_catalog.pg_largeobject          TO shop_reader;
-- GRANT SELECT ON pg_catalog.pg_largeobject_metadata TO shop_reader;
