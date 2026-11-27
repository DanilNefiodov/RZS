package com.ua.gamestore.web;

import com.ua.gamestore.exception.GameNotFoundException;
import com.ua.gamestore.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Глобальна обробка помилок.
 *
 * {@code @RestControllerAdvice} застосовує {@code @ExceptionHandler}-методи до УСІХ
 * контролерів в одному місці. Кожна помилка повертається в одному форматі {@link ErrorResponse}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Гру не знайдено → 404. */
    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(GameNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
    }

    /** Тіло не пройшло {@code @Valid} → 400 з переліком проблем по полях. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .sorted()
                .toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Помилка валідації вхідних даних", details);
    }

    /** Тіло не вдалося прочитати (зламаний JSON, текст замість числа) → 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnreadableBody(HttpMessageNotReadableException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Некоректне тіло запиту: очікується JSON гри");
    }

    /** Параметр шляху має неправильний тип (напр. /api/games/abc) → 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Параметр '" + ex.getName() + "' має неправильний формат: " + ex.getValue());
    }
}
