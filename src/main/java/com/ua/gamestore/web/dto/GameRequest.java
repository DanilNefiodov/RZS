package com.ua.gamestore.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Вхідні дані для створення або оновлення гри (тіло запиту POST/PUT).
 *
 * Без id: при створенні його призначає сервер, при оновленні він береться зі шляху запиту.
 * Правила валідації перевіряються, коли в контролері тіло позначене {@code @Valid}.
 */
public record GameRequest(
        @NotBlank(message = "назва обов'язкова")
        String title,

        @NotBlank(message = "розробник обов'язковий")
        String developer,

        @NotNull(message = "ціна обов'язкова")
        @Positive(message = "ціна має бути додатною")
        BigDecimal price,

        @Positive(message = "рік виходу має бути додатним")
        Integer releaseYear,

        @NotBlank(message = "платформа обов'язкова")
        String platform,

        @NotBlank(message = "жанр обов'язковий")
        String genre,

        @Min(value = 0, message = "вікове обмеження не може бути від'ємним")
        @Max(value = 18, message = "вікове обмеження не більше 18")
        Integer ageRating,

        String coverUrl,

        String description
) {
}
