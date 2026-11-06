#!/bin/bash

./mvnw clean compile

./mvnw exec:java -Dexec.mainClass="org.example.Main"
