# Магазин відеоігор (gamestore)

Навчальний проєкт на Spring Boot 3.4, Java 21, Maven. Поки що лише backend, дані зберігаються в пам'яті.

## Ендпоінти

| Метод  | Шлях                    | Що робить                              | Коди        |
|--------|-------------------------|----------------------------------------|-------------|
| GET    | `/api/games`            | перелік карток ігор                    | 200         |
| GET    | `/api/games?genre=...`  | перелік ігор одного жанру              | 200         |
| GET    | `/api/games/{id}`       | одна гра з деталями                    | 200 / 404   |
| POST   | `/api/games`            | створити гру                           | 201         |
| PUT    | `/api/games/{id}`       | оновити гру                            | 200 / 404   |
| DELETE | `/api/games/{id}`       | видалити гру                           | 204 / 404   |

Приклади `curl` для всіх операцій — у [requests.md](requests.md).

## Модель `Game`

| Поле          | Тип          | Де видно           |
|---------------|--------------|--------------------|
| `id`          | `Long`       | картка, сторінка   |
| `title`       | `String`     | картка, сторінка   |
| `platform`    | `String`     | картка, сторінка   |
| `price`       | `BigDecimal` | картка, сторінка   |
| `coverUrl`    | `String`     | картка, сторінка   |
| `developer`   | `String`     | сторінка           |
| `releaseYear` | `Integer`    | сторінка           |
| `genre`       | `String`     | сторінка, фільтр   |
| `ageRating`   | `Integer`    | сторінка (PEGI)    |
| `description` | `String`     | сторінка           |

Для створення й оновлення використовується окремий вхідний DTO `GameRequest` — ті самі поля, але **без `id`**.

## Структура

```
com.ua.gamestore
├── config       GameSeeder — початкові ігри
├── domain       Game — доменна модель
├── exception    GameNotFoundException → 404
├── repository   GameRepository (інтерфейс) + InMemoryGameRepository
├── service      GameService (інтерфейс) + GameServiceImpl
│   └── mapper   GameMapper — Game ↔ DTO
└── web          GameController
    └── dto      GameCardDto, GameDetailDto, GameRequest
```

## Запуск

```bash
./mvnw spring-boot:run
```

Тести:

```bash
./mvnw test
```
