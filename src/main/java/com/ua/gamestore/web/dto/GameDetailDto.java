package com.ua.gamestore.web.dto;

import java.math.BigDecimal;

/**
 * Повна інформація про гру — для сторінки гри.
 */
public record GameDetailDto(Long id, String title, String developer, BigDecimal price,
                            Integer releaseYear, String platform, String genre,
                            Integer ageRating, String coverUrl, String description) {
}
