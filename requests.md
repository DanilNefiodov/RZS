# Приклади запитів — Магазин відеоігор (заняття 1.2–1.4: CRUD, валідація, помилки)

Бекенд має бути запущений (порт 8080):

```bash
./mvnw spring-boot:run
```

Команди нижче запускати **з папки проєкту**. Кожна команда — в один рядок, тож вона
працює і в bash, і в `cmd`, і в PowerShell. У PowerShell пишіть `curl.exe` замість `curl`.

| # | Операція | Метод і шлях | Успіх | Помилка |
|---|---|---|---|---|
| 1 | Перелік ігор | `GET /api/games` | 200 | — |
| 2 | Фільтр за жанром | `GET /api/games?genre=...` | 200 | — |
| 3 | Одна гра | `GET /api/games/{id}` | 200 | 404 |
| 4 | Створити | `POST /api/games` | 201 | — |
| 5 | Оновити | `PUT /api/games/{id}` | 200 | 404 |
| 6 | Видалити | `DELETE /api/games/{id}` | 204 | 404 |
| 7 | Невалідні дані | `POST` / `PUT` з порушенням правил | — | 400 |

> **Кирилиця і Windows.** Якщо писати кирилицю прямо в команді `curl`, Windows може
> передати її не в UTF-8 — сервер відповість `400` або нічого не знайде. Тому:
> - у фільтрі жанр закодовано (`%D0%A8%D1%83%D1%82%D0%B5%D1%80` = `Шутер`);
>   у браузері можна писати просто `?genre=Шутер` — він закодує сам;
> - тіла запитів `POST`/`PUT` лежать у файлах [`requests/new-game.json`](requests/new-game.json)
>   та [`requests/update-game.json`](requests/update-game.json) (UTF-8).

---

## 1. Перелік ігор

```bash
curl http://localhost:8080/api/games
```

Очікувано `200` і масив карток:
```json
[
  { "id": 1, "title": "S.T.A.L.K.E.R. 2: Heart of Chornobyl", "platform": "PC", "price": 1299.00, "coverUrl": "/covers/stalker-2.jpg" },
  { "id": 2, "title": "Metro Exodus", "platform": "PlayStation 5", "price": 649.00, "coverUrl": "/covers/metro-exodus.jpg" }
]
```

---

## 2. Фільтр за жанром

```bash
curl "http://localhost:8080/api/games?genre=%D0%A8%D1%83%D1%82%D0%B5%D1%80"
```

Очікувано `200` і лише шутери (S.T.A.L.K.E.R. 2 та Metro Exodus). Регістр не важливий:
`шутер` і `Шутер` дають той самий результат. Без параметра `genre` повертаються всі ігри.

---

## 3. Одна гра з деталями

```bash
curl http://localhost:8080/api/games/1
```

Очікувано `200`:
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

Неіснуюча гра → `404` з тілом помилки (див. розділ 8):
```bash
curl -i http://localhost:8080/api/games/999
```
```json
{ "status": 404, "error": "Not Found", "message": "Гру з id=999 не знайдено", "details": [] }
```

---

## 4. Створити гру → 201

```bash
curl -i -X POST http://localhost:8080/api/games -H "Content-Type: application/json" --data-binary "@requests/new-game.json"
```

Очікувано `HTTP/1.1 201` і створена гра з новим `id` (після стартових п'яти — `6`):
```json
{ "id": 6, "title": "Hollow Knight", "developer": "Team Cherry", "price": 349.00, "...": "..." }
```

`id` у тілі запиту не передається — його призначає сервер.

---

## 5. Оновити гру → 200 / 404

```bash
curl -i -X PUT http://localhost:8080/api/games/2 -H "Content-Type: application/json" --data-binary "@requests/update-game.json"
```

Очікувано `HTTP/1.1 200` і гра з тим самим `id: 2`, але новими полями
(`"title": "Metro Exodus Gold Edition"`, `"price": 899.00`).

Оновлення неіснуючої гри → `404`:
```bash
curl -i -X PUT http://localhost:8080/api/games/999 -H "Content-Type: application/json" --data-binary "@requests/update-game.json"
```

---

## 6. Видалити гру → 204 / 404

```bash
curl -i -X DELETE http://localhost:8080/api/games/5
```

Очікувано `HTTP/1.1 204` без тіла. Повторне видалення тієї ж гри (або неіснуючої) → `404`:
```bash
curl -i -X DELETE http://localhost:8080/api/games/5
```

---

## 7. Невалідні дані → 400

Тіло `POST`/`PUT` перевіряється (`@Valid` у контролері, правила в `GameRequest`):

| Поле | Правило |
|---|---|
| `title`, `developer`, `platform`, `genre` | не порожні (`@NotBlank`) |
| `price` | обов'язкова й більша за 0 (`@NotNull`, `@Positive`) |
| `releaseYear` | більший за 0 (`@Positive`) |
| `ageRating` | від 0 до 18 (`@Min`, `@Max`) |

Файл [`requests/invalid-game.json`](requests/invalid-game.json) порушує одразу чотири правила
(порожні назва й жанр, від'ємна ціна, вік 21):

```bash
curl -i -X POST http://localhost:8080/api/games -H "Content-Type: application/json" --data-binary "@requests/invalid-game.json"
```

Очікувано `HTTP/1.1 400`, гра **не** створюється, у `details` — усі порушені правила:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Помилка валідації вхідних даних",
  "details": [
    "ageRating: вікове обмеження не більше 18",
    "genre: жанр обов'язковий",
    "price: ціна має бути додатною",
    "title: назва обов'язкова"
  ]
}
```

Ті самі правила діють для `PUT`.

---

## 8. Формат помилок

Усі помилки API повертаються в одному форматі (`ErrorResponse`, формує `GlobalExceptionHandler`):

| Поле | Зміст |
|---|---|
| `status` | HTTP-код (`404`, `400`) |
| `error` | назва коду (`Not Found`, `Bad Request`) |
| `message` | що сталося, українською |
| `details` | перелік проблем валідації (для інших помилок — порожній) |

| Ситуація | Приклад | Код |
|---|---|---|
| Гри з таким id немає | `GET`/`PUT`/`DELETE /api/games/999` | 404 |
| Тіло не пройшло валідацію | `POST` з [`requests/invalid-game.json`](requests/invalid-game.json) | 400 |
| Id — не число | `curl -i http://localhost:8080/api/games/abc` | 400 |
| Зламаний JSON або текст замість числа | `{"title":` або `"price": "дешево"` | 400 |

> Дані зберігаються в пам'яті: після перезапуску застосунку знову будуть лише 5 стартових ігор.
