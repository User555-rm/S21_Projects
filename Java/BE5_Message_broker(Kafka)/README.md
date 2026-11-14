Брокер сообщений (Apache Kafka) + распределённый кэш Redis:
- Построил event-driven интеграцию: сервис-генератор по таймеру (каждые 5 сек) выбирает случайный товар, меняет его цену (±10–25%) и остаток (±1–3) и публикует событие в Kafka (топик `product.events`, JSON); API-консьюмер (consumer-группа `api-product-events-group`) применяет изменение в БД, пишет историю в `product_events` и рассылает обновления.
- Real-time доставка клиентам по WebSocket (`/ws/products`) и SSE (`/sse/products`); написал HTML/JS эхо-клиенты для обоих каналов (`ws-client.html`, `sse-client.html`).
- Фотографии товаров кэшируются в Redis (TTL 10 мин) с инвалидацией при изменении.
- Инфраструктура в Docker Compose: postgres + kafka (KRaft, single-node) + redis + api + generator с healthcheck-зависимостями.