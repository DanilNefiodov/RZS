# Магазин відеоігор (gamestore)

Навчальний проєкт на Spring Boot 3.4, Java 21, Maven. Поки що лише backend.

## Ендпоінти

| Метод | Шлях              | Що повертає                     |
|-------|-------------------|---------------------------------|
| GET   | `/api/games`      | перелік карток ігор             |
| GET   | `/api/games/{id}` | одна гра з деталями, або 404    |

Приклади `curl` — у [requests.md](requests.md).

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
| `genre`       | `String`     | сторінка           |
| `ageRating`   | `Integer`    | сторінка (PEGI)    |
| `description` | `String`     | сторінка           |

## Структура

```
com.ua.gamestore
├── config       GameSeeder — початкові ігри
├── domain       Game — доменна модель
├── exception    GameNotFoundException → 404
├── repository   GameRepository (інтерфейс) + InMemoryGameRepository
├── service      GameService (інтерфейс) + GameServiceImpl
│   └── mapper   GameMapper — Game → DTO
└── web          GameController
    └── dto      GameCardDto, GameDetailDto
```

## Запуск

```bash
./mvnw spring-boot:run
```

Тести:

```bash
./mvnw test
```
