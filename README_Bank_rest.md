<h1>💳 Bank REST — система управления банковскими картами</h1>

<h2>🔎 Что это</h2>
<p>
  Backend-приложение на Java (Spring Boot) для управления банковскими картами, пользователями и переводами.
  Авторизация по JWT, роли <code>ADMIN</code> и <code>USER</code>, миграции БД через Liquibase,
  документация API в формате OpenAPI/Swagger.
<br/>
  Точка входа: Spring Boot приложение, база данных: PostgreSQL.
</p>

<h2>🧠 Бизнес-логика (коротко)</h2>
<ul>
  <li><b>Регистрация и вход</b>: <code>/v1/api/sign-up</code> (регистрация, возвращает пару токенов), <code>/v1/api/sign-in</code>, <code>/v1/api/refresh</code>.</li>
  <li><b>Карты</b>:
    <ul>
      <li><code>POST /v1/api/cards</code> — создать карту (ADMIN).</li>
      <li><code>GET /v1/api/cards</code> — постраничный список всех карт (ADMIN).</li>
      <li><code>GET /v1/api/cards/{id}</code> — получить карту по ID.</li>
      <li><code>GET /v1/api/cards/my</code> — постранично получить карты текущего пользователя (USER).</li>
      <li><code>GET /v1/api/cards/my/{id}</code> — получить свою карту по ID (USER).</li>
      <li><code>POST /v1/api/cards/{id}/status</code> — изменить статус карты (ADMIN).</li>
      <li><code>POST /v1/api/cards/{id}/add</code> — пополнить баланс карты (ADMIN).</li>
      <li><code>DELETE /v1/api/cards/{id}</code> — удалить карту (ADMIN).</li>
    </ul>
  </li>
  <li><b>Пользователи</b>: <code>GET /v1/api/users</code> — постраничный список пользователей (ADMIN).</li>
  <li><b>Переводы</b>: <code>POST /v1/api/transfers/own</code> — перевод между собственными картами пользователя (USER).</li>
</ul>

<h2>🔍 Подробности по эндпоинтам и логике</h2>
<h3>Auth / Registration</h3>
<ul>
  <li><code>POST /v1/api/sign-up</code> — регистрация нового пользователя. Валидация: корректный email, пароль 4–50 символов. Возвращает пару токенов (access/refresh). Роль после регистрации — USER.</li>
  <li><code>POST /v1/api/sign-in</code> — аутентификация по email/паролю. При успехе возвращает пару токенов. При неверных данных — 401.</li>
  <li><code>POST /v1/api/refresh</code> — принимает refreshToken и выдаёт новую пару токенов. При просроченном/некорректном токене — 401.</li>
  <li>Авторизация: передавайте <code>Authorization: Bearer &lt;accessToken&gt;</code> для защищённых эндпоинтов.</li>
  <li>Безопасность: токены подписываются секретами, заданными в переменных окружения (см. раздел ниже).</li>
  
</ul>

<h3>Карты</h3>
<ul>
  <li><code>POST /v1/api/cards</code> (ADMIN) — создаёт карту для пользователя. Проверяется формат номера (16 цифр), срок действия (YearMonth, в будущем), наличие пользователя. Возвращает 201 и заголовок <code>Location</code> с URI новой карты.</li>
  <li><code>GET /v1/api/cards</code> (ADMIN) — постраничный список всех карт. Параметры: <code>page</code>, <code>size</code>, <code>sort</code> (например, <code>id,asc</code>).</li>
  <li><code>GET /v1/api/cards/{id}</code> — получить карту по ID. Доступ не требует роли ADMIN, но бизнес-валидация на уровне сервиса может ограничивать доступ (например, скрывать чужие данные).</li>
  <li><code>GET /v1/api/cards/my</code> (USER) — постраничный список карт текущего пользователя. Параметры пагинации аналогичны.</li>
  <li><code>GET /v1/api/cards/my/{id}</code> (USER) — получить свою карту по ID. При попытке доступа к чужой карте — 404/403 в зависимости от правил.</li>
  <li><code>POST /v1/api/cards/{id}/status</code> (ADMIN) — изменить статус карты (ACTIVE/BLOCKED/EXPIRED). Валидация статуса и существования карты.</li>
  <li><code>POST /v1/api/cards/{id}/add</code> (ADMIN) — пополнение баланса карты на положительную сумму. Контроль ввода (сумма > 0), проверка существования карты.</li>
  <li><code>DELETE /v1/api/cards/{id}</code> (ADMIN) — удалить карту по ID. Возвращает 204 при успехе.</li>
</ul>

<h3>Переводы</h3>
<ul>
  <li><code>POST /v1/api/transfers/own</code> (USER) — перевод между собственными картами пользователя. Валидация: положительная сумма, обе карты принадлежат текущему пользователю, на карте-источнике достаточно средств, корректные статусы карт.</li>
  <li>Результат: DTO с ID перевода, картами-участниками, суммой и временем создания.</li>
</ul>

<h3>Пользователи</h3>
<ul>
  <li><code>GET /v1/api/users</code> (ADMIN) — постраничный список пользователей. Используется для администрирования.</li>
</ul>

<h3>Ошибки и валидация</h3>
<ul>
  <li>Валидация тела запроса — Jakarta Validation (аннотации на DTO).</li>
  <li>Типовые ответы об ошибках: 400 (валидация), 401 (неавторизовано), 403 (нет прав), 404 (не найдено).</li>
  <li>Сообщения об ошибках локализованы в DTO и/или формируются глобальным обработчиком исключений.</li>
</ul>

<h2>📜 Swagger / OpenAPI</h2>
<ul>
  <li><b>Swagger UI</b>: откроется после запуска приложения по адресу: <code>http://localhost:8080/swagger-ui/index.html</code></li>
  <li><b>OpenAPI YAML (статический)</b>: <code>http://localhost:8080/openapi.yaml</code> (файл: <code>src/main/resources/static/openapi.yaml</code>)</li>
  <li><b>OpenAPI YAML (docs)</b>: файл спецификации также лежит в <code>docs/openapi.yaml</code></li>
</ul>

<h2>🐘 База данных</h2>
<ul>
  <li>PostgreSQL поднимается через Docker Compose (сервис <code>db</code>).</li>
  <li>Миграции выполняет Liquibase автоматически при старте приложения (<code>spring.liquibase</code>).</li>
</ul>

<h2>🐳 Запуск через Docker Compose</h2>
<ol>
  <li>Установите Docker и Docker Compose.</li>
  <li>В корне проекта выполните:
    <pre><code>docker compose up -d --build</code></pre>
  </li>
  <li>Приложение будет доступно на <code>http://localhost:8080</code>.
    БД — на порту <code>${DATABASE_PORT:-5435}</code> хоста (по умолчанию 5435).</li>
</ol>

<h3>Переменные окружения (docker-compose.yml)</h3>
<ul>
  <li><code>DATABASE_USER</code>, <code>DATABASE_PASSWORD</code>, <code>DATABASE_NAME</code> — настройки БД (по умолчанию: admin/admin/bank_rest_db)</li>
  <li><code>SERVER_PORT</code> — внешний порт приложения (по умолчанию 8080)</li>
  <li>Datasource в контейнере приложения: <code>jdbc:postgresql://db:5432/${DATABASE_NAME}</code></li>
  <li>Пример файла переменных окружения: <code>.env.prod.example</code> (в корне проекта). Скопируйте его в <code>.env</code> и при необходимости измените значения.</li>
</ul>

<h2>🔐 Безопасность</h2>
<ul>
  <li>JWT (Bearer) — добавляйте заголовок <code>Authorization: Bearer &lt;accessToken&gt;</code>.</li>
  <li>Роли: <code>ADMIN</code> (админ-операции) и <code>USER</code> (операции со своими ресурсами).</li>
  <li><b>Дефолтный администратор</b>: <code>admin@admin.com</code> / пароль <code>admin</code> (создаётся миграцией при старте).</li>
  <li><b>Мок-пользователь</b>: <code>user@example.com</code> / пароль <code>user</code> (добавляется миграцией <code>008-insert-mock-data.yml</code> для быстрых проверок).</li>
</ul>

<h2>✅ Что выполнено по ТЗ</h2>
<ul>
  <li>Аутентификация и авторизация: Spring Security + JWT, роли ADMIN/USER.</li>
  <li>Функционал карт: создание, просмотр, изменение статуса, удаление, пополнение, пагинация.</li>
  <li>Переводы между своими картами пользователя.</li>
  <li>Управление пользователями (список, пагинация) для ADMIN.</li>
  <li>Валидация входных данных (Jakarta Validation), обработка ошибок.</li>
  <li>Liquibase миграции.</li>
  <li>Документация API: Swagger UI + OpenAPI спецификация (<code>docs/openapi.yaml</code> и статический <code>src/main/resources/static/openapi.yaml</code>).</li>
  <li>Docker Compose для локального развёртывания.</li>
</ul>

<h2>🧪 Быстрый smoke-тест</h2>
<ol>
  <li>Зарегистрируйтесь: <code>POST /v1/api/sign-up</code> → получите пару токенов.</li>
  <li>Откройте Swagger UI и авторизуйтесь с помощью accessToken (кнопка Authorize).</li>
  <li>Как ADMIN создайте карту: <code>POST /v1/api/cards</code>.</li>
  <li>Как USER посмотрите свои карты: <code>GET /v1/api/cards/my</code>.</li>
  <li>Создайте перевод между своими картами: <code>POST /v1/api/transfers/own</code>.</li>
</ol>

<h2>⚙️ Технологии</h2>
<p>Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Liquibase, PostgreSQL, JWT, Swagger (OpenAPI), Docker</p>
