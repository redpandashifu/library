# Библиотека

## Описание
Приложение содержит данные о книгах и читателях также позволяет вести учет выданных читателям книг. 
Приложение построено на фреймворке SpringBoot(2.3.3.RELEASE), Spring(5.2.8.RELEASE). 
Архитектурно разделено на три слоя:
1. Слой представления данных: REST контроллеры представляют пользователю интерфейс для создания, чтения, обновления, удаления данных
2. Бизнес слой: сервисы для выполнения бизнес логики
3. Слой доступа к данным: посредством репозиториев происходит работа с базой данных

## Сборка и запуск
1. Приложение собирается командой: 
   mvn clean package
2. Запуск приложения:
   java -jar Library-1.0-SNAPSHOT.jar


## Обязательные требования
1. В качестве базы данных используется H2 in-memory

Таблицы:
author - авторы книг
book -  книги
book_authors - скрытая таблица для связи авторов и книг 
reader - читатели
taken_book - выданные читателям книги

Связи между таблицами:
book связана отношением ManyToMany c author посредством таблицы book_authors
book связана отношением OneToMany c takenBook
reader связана отношением OneToMany c takenBook

2. Пользовательский интерфейс реализован на основе Rest CRUD запросов:
/author - для авторов
   curl -v -X POST http://localhost:8080/library/author -H 'Content-Type: application/json' -d '{"firstName": "Lewis","lastName": "Carroll"}'
   curl -v -X PUT http://localhost:8080/library/author -H 'Content-Type: application/json' -d '{"id": 1, "firstName": "Lew","lastName": "Carroll"}'
   curl -v -X GET http://localhost:8080/library/author?id=1 -H 'Content-Type: application/json'
   curl -v -X DELETE http://localhost:8080/library/author?id=1 -H 'Content-Type: application/json'

/book - для книг
   curl -v -X POST http://localhost:8080/library/book -H 'Content-Type: application/json' -d '{"title": "Pippi Longstocking","published": 1946,"authorIds": [1]}'
   curl -v -X PUT http://localhost:8080/library/book -H 'Content-Type: application/json' -d '{"id": 2, "title": "Pippi Longstocking","published": 1945,"authorIds": [1]}'
   curl -v -X GET http://localhost:8080/library/book?id=2 -H 'Content-Type: application/json'
   curl -v -X DELETE http://localhost:8080/library/book?id=2 -H 'Content-Type: application/json'

/reader - для читателей
   curl -v -X POST http://localhost:8080/library/reader -H 'Content-Type: application/json' -d '{"firstName":"Иван","middleName":"Иванович", "lastName":"Иванов", "gender":"MALE", "birthday":"1980-02-29"}'
   curl -v -X PUT http://localhost:8080/library/reader -H 'Content-Type: application/json' -d '{"id": 3, "firstName":"Иван","middleName":"Иванович", "lastName":"Иванов", "gender":"MALE", "birthday":"1980-02-28"}'
   curl -v -X GET http://localhost:8080/library/reader?id=3 -H 'Content-Type: application/json'
   curl -v -X DELETE http://localhost:8080/library/reader?id=3 -H 'Content-Type: application/json'

/takenBook - для выданных читателю книг
   curl -v -X POST http://localhost:8080/library/takenBook -H 'Content-Type: application/json' -d '{"readerId": 3,"bookId": 2, "dateFrom": "2024-03-03","dateTo": null}'
   curl -v -X PUT http://localhost:8080/library/takenBook -H 'Content-Type: application/json' -d '{"id":4,"readerId":3,"bookId":2,"dateFrom":"2024-03-03","dateTo":"2024-03-23"}'
   curl -v -X GET http://localhost:8080/library/takenBook?id=4 -H 'Content-Type: application/json'
   curl -v -X GET 'http://localhost:8080/library/takenBook/findByPeriod?readerId=3&from=2024-03-01&to=2024-03-10' -H 'Content-Type: application/json'
   curl -v -X DELETE http://localhost:8080/library/takenBook?id=4 -H 'Content-Type: application/json'

3. Используемые библиотеки доступны для скачивания через maven репозиторий.

## Дополнительно
1. Сформирован отчет о выданных книгах за выбранный период
/takenBook/findByPeriod - выводит отчет о количестве выданных читателю книг.
   curl -v -X GET 'http://localhost:8080/library/takenBook/findByPeriod?readerId=3&from=2024-03-01&to=2024-03-10' -H 'Content-Type: application/json'
 
2. Для работы с БД используется ORM Hibernate, который явлется в SpringBoot ORM по умолчанию.
Посмотреть содержимое таблиц, которые создал Hibernate можно в консоли по адресу: http://localhost:8080/library/h2-console
Логин и пароль в application.properties.
3. Для соединения с базой используется пул соединения HikariPool (пул соединений по умолчанию в SpringBoot).  
4. Реализован интеграционный тест на тестовой БД по отчету выданных книг за выбранный период с использованием junit.
5. Добавлено логирование в консоль.

## Что еще нужно сделать
1. Добавить юнит тесты для сервисов с mock’ами репозиториев, инеграционные тесты для сервисов.
2. Более понятные сообщения на REST запросы при 4xx и 5xx ошибках
