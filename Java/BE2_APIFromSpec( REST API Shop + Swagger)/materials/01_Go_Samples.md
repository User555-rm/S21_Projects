#### **Go**
We start by describing models. Go doesn't have the usual OOP classes and objects, so we describe structures.

```go
type ExampleModel {
    ID          string `json:"id"`
    ExampleModelAttr string `json:"example_model_attr"`
}
```

Do not forget to import all the necessary packages.

```go
package main

import (
  "log"
  "net/http"
  "math/rand"
  "strconv"
  "encoding/json"
  "github.com/gorilla/mux"
)
```

Now let's describe the methods that handle HTTP-requests. Let it be the getModels method, which gets all the models. This method receives ResponseWriter and pointer to Request.

```go
var models []models

func getModels(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(models)
}
```

Now let's look at the entry point.

```go
func main() {
    r := mux.NewRouter();
    ...
    r.HandleFunc("/models", getModels).Methods("GET")
    log.Fatal(http.ListenAndServe(":8000", r))
}
```
