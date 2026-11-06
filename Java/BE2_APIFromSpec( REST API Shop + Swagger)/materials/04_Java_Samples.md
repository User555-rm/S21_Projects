#### **Java**
Creating an HTTP API application in Java also starts with the model description. We will use the Spring Boot library.

```java
public class ExampleModel {
    private Integer id;
    private String exampleModelAttr;

    public Integer getId() {
       return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public String getExampleModelAttr() {
       return id;
    }

    public void setExampleModelAttr(String id) {
       this.id = id;
    }
}
```

Now let's describe the interface and its implementation — the service.

```java
public interface ExampleService {
    void create(ExampleModel model);
    void DoExampleBusiness();
}
```

```java
@Service
public class ExampleServiceImpl implements ExampleService {
    private static final ExampleRepository  exampleRepository = new ExampleRepositoryImpl();
    private static final AtomicInteger modelIdHolder = new AtomicInteger();

    @Override
    public void create(ExampleModel model) {
        final int modelId = modelIdHolder.incrementAndGet();
        model.setId(modelId);
        exampleRepository.create(model);
    }

    @Override
    public void DoExampleBusiness() {
        // Business logic
    }
}
```

Now let's write a controller.

```java
@RestController
public class ExampleController {
    private final ExampleService exampleService;

    @Autowired
    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }
}
```

Autowired indicates that Dependency Injection is used.

Now let's write the controller methods.

```java
@PostMapping(value = "/model")
public ReponseEntity<?> create(@RequestBody ExampleModel model) {
    exampleService.create(model);
    return new ReponseEntity<>(HttpStatus.CREATED);
}
```

PostMapping indicates that POST request can be sent to /model, which will be handled by this method.