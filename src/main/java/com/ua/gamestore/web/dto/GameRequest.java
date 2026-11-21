package com.ua.gamestore.web.dto;

import java.math.BigDecimal;

/**
 * Вхідні дані для створення або оновлення гри (тіло запиту POST/PUT).
 *
 * Без id: при створенні його призначає сервер, при оновленні він береться зі шляху запиту.
 */
public record GameRequest(
        String title,
        String developer,
        BigDecimal price,
        Integer releaseYear,
        String platform,
        String genre,
        Integer ageRating,
        String coverUrl,
        String description
) {
}
