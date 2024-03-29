
# Security
Для обеспечения безопасности и управления аутентификацией и авторизацией наше приложение использует Spring Security который интегрирован с сервером авторизации Keycloak. Конфигурация секьюрити находится в gazon-service/security. Включается / отключается секьюрность в методе configure.
# Keycloak
Keycloak - это сервер авторизации с открытым исходным кодом, который предоставляет функциональность аутентификации и авторизации. В контексте нашего приложения, Keycloak используется для аутентификации пользователей и управления доступом к защищенным ресурсам. Он позволяет нам делегировать процесс аутентификации и авторизации на сторону сервера. Можно считать, что Keycloak это отдельный микросервис, написанный за нас, который разворачивается вместе с остальными приложениями, и к которому они обращаются для проверки пользователей.

При запросе к закрытому ресурсу, юзера перебрасывает на страницу аутентификации в keycloak. После успешной аутентификации, keycloak выдает access token и refresh_token в формате JWT.
#### Основные термины:
- **Realm** - область для защиты, с возможность настройки со своими требованиями;
- **Client** - приложение или сервер, которые могут взаимодействовать с KeyCloak для аутентификации и авторизации;
- **Client scope** - область действия клиента. Позволяет управлять разрешениями для client. Информации об уровне доступа передается через JWT;
- **Users** - пользователи который могут пройти аутентификацию с помощью KeyCloak. Каждый User принадлежит определенной области, и иметь разные роли. Содержит такую информацию как username, firstname, lastname, email и так далее;
- **Groups** - группы, необходимые для упрощения управления доступом для большого количества пользователей. Способ сгруппировать набор пользователей и назначить им определенные атрибуты и роли.

# Запуск
Keycloak для нашего приложения запускается через Docker контейнере вместе с базой данных двумя способами:
1. Вместе с приложением (app, db, keycloak); ==пока не работает :(==
```
docker-compose -f docker-compose-keycloak.yml up
```
3. Отдельно от приложения, а приложение в IDEA
```
docker-compose -f docker-compose-keycloak-non-app.yml up
```
**Важно!** При первом запуске KeyCloak будет вылетать, пока не подключишь сервис. Поэтому после **docker-compose** запусти приложение **gazon-service**. И перезагрузи контейнер keycloak.

Консоль Keycloak [тут](http://localhost:8180/admin/master/console/#/GazonRealm). Логин **admin** пароль **admin**.

> Чтобы Keycloak мог использовать данные пользователей из нашей базы данных, написан Keycloak provider: **custom-user-provider** который при сборке образа копируется в контейнер. Все основные настройки Keycloak находятся в realm-export.json, при запуске контейнера файл импортируются в Keycloak.

# Получение токена
Для получения токена, с помощью Postman сделать POST запрос на [http://localhost:8180/realms/airline-realm/protocol/openid-connect/token](http://localhost:8180/realms/airline-realm/protocol/openid-connect/token) в теле запроса передать данные как на скриншоте: `grant_type:password client_id:gazon-service username:admin password:admin` или `username` и `password` любого пользователя из нашего приложения (как в примере):

![Pasted image 20240326201801.png](src%2Fmain%2Fresources%2Fscreenshots%2FPasted%20image%2020240326201801.png)
Результат:
![Pasted image 20240326202418.png](src%2Fmain%2Fresources%2Fscreenshots%2FPasted%20image%2020240326202418.png)

# Использования токена
Скопировать из ответа access_token и вставлять его при запросах к закрытым endpoint. Также укажи `type = Bearber Token`:
![Pasted image 20240326202511.png](src%2Fmain%2Fresources%2Fscreenshots%2FPasted%20image%2020240326202511.png)
Результат:
![Pasted image 20240326202547.png](src%2Fmain%2Fresources%2Fscreenshots%2FPasted%20image%2020240326202547.png)
Время жизни токена 5 минут. JWT токен себе содержит payload в котором могут передаваться данные пользователя, роли и так далее. Декодировать токен можно [тут](https://jwt.io/).

Для получения нового токена, сделать запрос в котором передать **refresh_token**:
![Pasted image 20240326203027.png](src%2Fmain%2Fresources%2Fscreenshots%2FPasted%20image%2020240326203027.png)

# Подробнее
1. [Пять простых шагов для понимания JSON Web Tokens (JWT)](https://habr.com/ru/articles/340146/);
2. [Реализация JWT в Spring Boot](https://struchkov.dev/blog/ru/jwt-implementation-in-spring/);
3. Также под капотом KeyCloak использует OAuth 2.0 и OpenID Connect. [Как работает OAuth 2.0 и OpenID Connect](https://struchkov.dev/blog/ru/how-oauth2-works/);
4. [Введение в KeyCloak](https://www.youtube.com/watch?v=duawSV69LDI&list=WL&index=29&ab_channel=StianThorgersen)
5. [Spring boot 3 Keycloak integration for beginners](https://www.youtube.com/watch?v=vmEWywGzWbA&list=WL&index=29&ab_channel=BoualiAli);
6. [Реализация собственного провайдера](https://www.baeldung.com/java-keycloak-custom-user-providers)