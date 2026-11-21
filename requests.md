# Приклади запитів — Магазин відеоігор (заняття 1.1)

Бекенд має бути запущений (порт 8080):

```bash
./mvnw spring-boot:run
```

На цьому етапі є лише **два GET-ендпоінти** (читання). Початкові ігри додаються
в коді — у класі `GameSeeder`.

---

## 1. Перелік ігор (картки)

```bash
curl http://localhost:8080/api/games
```

Очікувана відповідь (скорочено):
```json
[
  { "id": 1, "title": "S.T.A.L.K.E.R. 2: Heart of Chornobyl", "platform": "PC", "price": 1299.00, "coverUrl": "/covers/stalker-2.jpg" },
  { "id": 2, "title": "Metro Exodus", "platform": "PlayStation 5", "price": 649.00, "coverUrl": "/covers/metro-exodus.jpg" }
]
```

---

## 2. Одна гра з деталями

```bash
curl http://localhost:8080/api/games/1
```

Очікувана відповідь:
```json
{
  "id": 1,
  "title": "S.T.A.L.K.E.R. 2: Heart of Chornobyl",
  "developer": "GSC Game World",
  "price": 1299.00,
  "releaseYear": 2024,
  "platform": "PC",
  "genre": "Шутер",
  "ageRating": 18,
  "coverUrl": "/covers/stalker-2.jpg",
  "description": "Шутер з елементами виживання у відкритому світі Чорнобильської зони відчуження."
}
```

---

## 3. Гра, якої немає → 404

```bash
curl -i http://localhost:8080/api/games/999
```

Очікувано: перший рядок `HTTP/1.1 404`.
