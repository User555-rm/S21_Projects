#### **Python**
The most popular frameworks for implementing HTTP APIs in Python are Flask and Django.

Flask is a micro-framework for creating web applications and RESTful APIs. Its main task is handling HTTP requests and routing them to the appropriate function in the app. Here's an example of how to use Flask to create a RESTful API.

```python
# app.py
from flask import Flask, request, jsonify

app = Flask(__name__)

models = [
    { "id": 1, "exampleModelAttr": "attr1" },
    { "id": 2, "exampleModelAttr": "attr2" }
]

@app.get("/models")
def get_models():
    return jsonify(countries)

@app.post("/models")
def add_model():
    if not request.is_json:
        return { "error": "Request must be a json" }, 415
    model = request.get_json()
    model["id"] = new_id()
    models.append(model)
    return model, 201
```

Now let's take a look at Django, another popular framework for creating a REST API application.

First you need to create a Django project, then add the modules you use to settings.py.

```python
# examplemodelsapi/settings.py
INSTALLED_APPS = [
    "django.contrib.admin",
    "django.contrib.auth",
    "django.contrib.contenttypes",
    "django.contrib.sessions",
    "django.contrib.messages",
    "django.contrib.staticfiles",
    "rest_framework",
    "countries",
]
```

Add models.

```python
# examplemodels/models.py
from django.db import models

class ExampleModel(models.Model):
    id = models.IntegerField()
    exampleModelAttr = model.CharField(max_length=100)
```

Don't forget to apply migrations: `python manage.py makemigrations`, `python manage.py migrate`.

Write serializers.

```python
# examplemodels/serializers.py
from rest_framework import serializers
from .models import ExampleModel

class ExampleModelSerializer(serializers.ModelSerializer):
    class Meta:
        model = ExampleModel
        fields = ["id", "exampleModelAttr"]
```

Let's add the view code (something we haven't done in previous languages). This class generates the views needed to manage the data of the ExampleModel class.

```python
from rest_framework import viewsets

from .models import ExampleModel
from .serializers import ExampleModelSerializer

class ExampleModelViewSet(viewsets.ModelViewSet):
    serializer_class = ExampleModelSerializer
    queryset = ExampleModel.objects.all()
```

Now update urls.py.

```python
# examplemodels/urls.py
from django.urls import path, include
from rest_framework.routers import DefaultRouter

from .views import ExampleModelViewSet

router = DefaultRouter()
router.register(r"models", ExampleModelViewSet)

urlpatterns = [
    path("", include(router.urls))
]
```

```python
# examplemodelsapi/urls.py
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path("admin/", admin.site.urls),
    path("", include("examplemodels.urls")),
]
```

Yay! It's ready to go!: `python manage.py runserver`.