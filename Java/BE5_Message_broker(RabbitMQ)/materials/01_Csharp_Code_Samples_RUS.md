# Server Sent Events (SSE)
На бэкенде это выглядит так. Давай посмотрим на пример кода на C#.

Напишем SSE в качестве middleware-обработчика запроса:

```csharp
app.Use(async (context, next) =>
{
    if (context.Request.Path.ToString().Equals("/sse"))
    {
        var response = context.Response;
        response.Headers.Add("Content-Type", "text/event-stream");

        for(var i = 0; true; ++i)
        {
            // WriteAsync requires `using Microsoft.AspNetCore.Http`
            await response
                .WriteAsync($"data: Middleware {i} at {DateTime.Now}\r\r");

            await response.Body.FlushAsync();
            await Task.Delay(5 * 1000);
        }
    }

    await next.Invoke();
});
```

А теперь посмотрим, как это может выглядеть в качестве контроллера:

```csharp
[Route("/api/sse")]
public class ServerSentEventController : Controller
{
    [HttpGet]
    public async Task Get()
    {
        var response = Response;
        response.Headers.Add("Content-Type", "text/event-stream");

        for(var i = 0; true; ++i)
        {
            await response
                .WriteAsync($"data: Controller {i} at {DateTime.Now}\r\r");

            response.Body.Flush();
            await Task.Delay(5 * 1000);
        }
    }
}
```

С помощью заголовка «Content-Type = text/event-stream» мы указываем на то, что данные будут передаваться в потоке и нет необходимости в закрытии соединения.

Методы Body.Flush() и Body.FlushAsync() гарантируют, что данные будут сброшены в браузер, открывший соединение.

# Websockets

Рассмотрим пример реализации «слушателя» соединения по WebSockets на примере C# кода.

Принятие запросов WebSockets:

```csharp
app.Use(async (context, next) =>
{
    if (context.Request.Path == "/ws")
    {
        if (context.WebSockets.IsWebSocketRequest)
        {
            using var webSocket = await context.WebSockets.AcceptWebSocketAsync();
            await Echo(webSocket);
        }
        else
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
        }
    }
    else
    {
        await next(context);
    }

});

...

private static async Task Echo(WebSocket webSocket)
{
    var buffer = new byte[1024 * 4];
    var receiveResult = await webSocket.ReceiveAsync(
        new ArraySegment<byte>(buffer), CancellationToken.None);

    while (!receiveResult.CloseStatus.HasValue)
    {
        await webSocket.SendAsync(
            new ArraySegment<byte>(buffer, 0, receiveResult.Count),
            receiveResult.MessageType,
            receiveResult.EndOfMessage,
            CancellationToken.None);

        receiveResult = await webSocket.ReceiveAsync(
            new ArraySegment<byte>(buffer), CancellationToken.None);
    }

    await webSocket.CloseAsync(
        receiveResult.CloseStatus.Value,
        receiveResult.CloseStatusDescription,
        CancellationToken.None);
}
```

# Распределённый кэш

Следующий код включает кэш для Redis:

Приложение настраивает реализацию кэша с помощью экземпляра RedisCache (AddStackExchangeRedisCache).

Создай Кэш для Redis.
Скопируй основную строку подключения (StackExchange.Redis) в конфигурацию.

```csharp
public void ConfigureServices(IServiceCollection services)
{
    if (_hostContext.IsDevelopment())
    {
        services.AddDistributedMemoryCache();
    }
    else
    {
        services.AddStackExchangeRedisCache(options =>
        {
            options.Configuration = _config["MyRedisConStr"];
            options.InstanceName = "SampleInstance";
        });
    }

    services.AddRazorPages();
}
```

В приведенном выше коде предполагается, что основная строка подключения (StackExchange.Redis) была сохранена в конфигурации с именем MyRedisConStr ключа.
