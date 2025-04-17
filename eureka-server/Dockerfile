#FROM ubuntu:latest
#LABEL authors="b.bayakhmet"
#
#ENTRYPOINT ["top", "-b"]

# Используем официальный образ OpenJDK
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл сборки JAR в контейнер
COPY build/libs/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]