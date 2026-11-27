package com.ua.gamestore.web.dto;

import java.util.List;

/**
 * Єдиний формат тіла відповіді для всіх помилок API.
 *
 * Замість стандартної сторінки помилки Spring клієнт завжди отримує передбачуваний JSON:
 * код, тип помилки, повідомлення і (для валідації) перелік проблем.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, List.of());
    }
}
