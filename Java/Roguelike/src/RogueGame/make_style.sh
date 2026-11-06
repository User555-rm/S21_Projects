#!/bin/bash

./mvnw spotless:apply

./mvnw checkstyle:check
