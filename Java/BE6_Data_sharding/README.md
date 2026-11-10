Шардирование данных + OAuth 2.0 (командный): 
- Выделил микросервис image-service с горизонтальным шардированием PostgreSQL: 4 БД (`image_db_0..3`), по 4 DataSource / EntityManagerFactory / TransactionManager (HikariCP). 
- Реализовал детерминированный маршрутизатор шардов по первой hex-цифре UUID (без lookup-таблиц): `0-3`→шард 0 … `c-f`→шард 3; операции выполняются в транзакции нужного шарда. 
- API (gateway) обращается в image-service по HTTP через собственный `ImageClient`, наружу сервис не публикуется. 
- OAuth 2.0-вход через Google (Authorization Code flow), автосоздание пользователя и выдача JWT (jjwt) с JWT-фильтром для доступа к API; пароли — BCrypt.