## Security

Для обеспечения безопасности и управления аутентификацией и авторизацией наше приложение использует Spring Security
который интегрирован с сервером авторизации Keycloak.

# Запуск
В нашем проекте Keycloak запускается в docker контейнере вместе с базой данных командой <code>docker-compose -f docker-compose-non-app.yml up</code>. (в дальнейшем вы можете запускать одновременно БД и Keycloak с помощью этого способа, а не способом, описанном в [guide_docker.md](guide_docker.md))
Консоль keycloak [тут](http://localhost:8180). Логин admin пароль admin.

> **Важно!** При первом запуске KeyCloak будет вылетать, пока запустишь gazon-service. Это происходит из-за того, что KeyCloak не может найти нужные таблицы, которые появляются только после первого запуска приложения. Поэтому после **docker-compose** запусти приложение **gazon-service**. И перезагрузи контейнер keycloak.

# Получение токена
Для получения токена, с помощью Postman сделать POST запрос на http://localhost:8180/realms/GazonRealm/protocol/openid-connect/token в теле запроса передать данные как на скриншоте: `grant_type:password client_id:gazon-service username:admin1 password:admin` или `username` и `password` любого пользователя из нашего приложения (как в примере):

![Pasted image 20240326201801.png](images%2Fsecurity%2FPasted%20image%2020240326201801.png)
Результат:
![Pasted image 20240326202418.png](images%2Fsecurity%2FPasted%20image%2020240326202418.png)

# Использования токена
Скопировать из ответа <code>access_token</code> и вставлять его при запросах к защищенным эндпоинтам. Время жизни токена - 30 минут.
JWT токен содержит payload в котором могут передаваться данные пользователя, роли и тд. Декодировать токен можно [тут](https://jwt.io/).
Декодируя этот payload, приложения могут решить, что показывать пользователю. Например, проанализировав, что в payload содержится роль USER, приложение может запретить пользователю удалять ресурсы.
![Pasted image 20240326202511.png](images%2Fsecurity%2FPasted%20image%2020240326202511.png)

Чтобы подставить токен в [Swagger UI](http://localhost:8080/swagger-ui/index.html), необходимо нажать на кнопку Authorize, и вставить ранее полученный access token.

![swaggerUI.png](images%2Fsecurity%2FswaggerUI.png)![swaggerUI.png](./images/swaggerUI.png)

Для получения нового токена, сделать запрос в котором передать **refresh_token**:
![Pasted image 20240326203027.png](images%2Fsecurity%2FPasted%20image%2020240326203027.png)

## Отключение Security

Чтобы отключить security, в application.yml в проперти spring.profiles.active удалить профиль security

## Keycloak (Опциональный раздел)
Keycloak - это сервер аутентификации и авторизации. Иными словами, Keycloak - это приложение, которое запускается с остальными приложениями, разработчики которого за нас реализовали логику аутентификации и авторизации, нам лишь остается убедиться, что пользователь, делающий запрос к нашим приложениям, был аутентифицирован Keycloak'ом. Keycloak особенно важен в микросервисной архитектуре, так как если бы его не было, то нам пришлось бы самостоятельно писать приложение, выполняющее аналогичные функции.

При запросе к защищенному ресурсу, пользователя перебрасывает на страницу аутентификации в keycloak. После успешной аутентификации, keycloak выдает
access token и refresh_token. [Token](https://habr.com/ru/articles/340146/) - это строка, которая содержит в себе всю информацию об авторизованном пользователе. При каждом запросе к нашему приложеню клиент должен передавать этот токен, и при его успешной проверке, мы пропускаем запрос дальше.

#### Основные термины:
- **Realm** - область для защиты, с возможность настройки со своими требованиями;
- **Client** - приложение или сервер, которые могут взаимодействовать с KeyCloak для аутентификации и авторизации;
- **Client scope** - область действия клиента. Позволяет управлять разрешениями для client. Информации об уровне доступа передается через JWT;
- **Users** - пользователи который могут пройти аутентификацию с помощью KeyCloak. Каждый User принадлежит определенной области, и иметь разные роли. Содержит такую информацию как username, firstname, lastname, email и так далее;
- **Groups** - группы, необходимые для упрощения управления доступом для большого количества пользователей. Способ сгруппировать набор пользователей и назначить им определенные атрибуты и роли.

# Дополнительные материалы
1. [Реализация JWT в Spring Boot](https://struchkov.dev/blog/ru/jwt-implementation-in-spring/);
2. Также под капотом KeyCloak использует OAuth 2.0 и OpenID Connect. [Как работает OAuth 2.0 и OpenID Connect](https://struchkov.dev/blog/ru/how-oauth2-works/);
3. [Введение в KeyCloak](https://www.youtube.com/watch?v=duawSV69LDI&list=WL&index=29&ab_channel=StianThorgersen)
4. [Spring boot 3 Keycloak integration for beginners](https://www.youtube.com/watch?v=vmEWywGzWbA&list=WL&index=29&ab_channel=BoualiAli);
5. [Реализация собственного провайдера](https://www.baeldung.com/java-keycloak-custom-user-providers)