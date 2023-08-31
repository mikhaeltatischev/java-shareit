# ShareIt
Веб-сервис для "шеринга".  

С помощью сервиса можно поделиться или взять вещь на время, так же можно оставить запрос на аренду вещи, если нужной не нашлось.  
## Два сервиса
1. Gateway: является точкой входа, обрабатывает запросы от пользователей.

2. Server: отвечает за основную функциональность, такую как создание, управление, бронирование вещей.

## Стек
<p>
  <img src="https://img.shields.io/badge/Java-red" />
  <img src="https://img.shields.io/badge/Spring%20boot-light green" />
  <img src="https://img.shields.io/badge/Spring%20validation-light green" />
  <img src="https://img.shields.io/badge/Mockito-green" />
  <img src="https://img.shields.io/badge/Hibernate-yellow" />
  <img src="https://img.shields.io/badge/PostgreSQL-blue" />
  <img src="https://img.shields.io/badge/Lombok-orange" />
  <img alt="Docker" src="https://img.shields.io/badge/-Docker-46a2f1?style=flat-square&logo=docker&logoColor=white" />
</p>

## Запуск приложения
Для развертывания проекта используется Docker-compose, в терминале нужно открыть директорию проекта и ввести команду ```docker-compose up```.  
Используемая версия языка Java - 11.  
