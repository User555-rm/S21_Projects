# Server Sent Events (SSE)
 Let's look at an example of the code in C#.

Let's write SSE as a middleware request handler:

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

Now let's see what it might look like as a controller:

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

With the Content-Type = text/event-streamheader — we indicate that the data will be tranferred in a stream and there is no need to close the connection.

 Body.Flush() and Body.FlushAsync() methods guarantee that the data will be sent to the browser that opened the connection.

# Websockets

Let's look at a "listener" implementation of a WebSocket connection using C# code as an example.

Accepting WebSockets requests:

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

# Distributed cache

The following code includes cache for Redis:

The application configures the cache implementation using an instance of RedisCache (AddStackExchangeRedisCache).

Create cache for Redis.
Copy the main connection string (StackExchange.Redis) into the configuration.

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

The above code assumes that the main connection string (StackExchange.Redis) has been stored in a configuration named MyRedisConStr key.
