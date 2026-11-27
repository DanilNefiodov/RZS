package com.ua.gamestore.exception;

/**
 * Гру не знайдено.
 *
 * Код 404 і тіло відповіді формує {@link com.ua.gamestore.web.GlobalExceptionHandler},
 * тому {@code @ResponseStatus} тут більше не потрібен.
 */
public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(Long id) {
        super("Гру з id=" + id + " не знайдено");
    }
}
