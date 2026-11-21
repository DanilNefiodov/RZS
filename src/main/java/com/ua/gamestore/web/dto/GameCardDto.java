package com.ua.gamestore.web.dto;

import java.math.BigDecimal;

/**
 * Картка гри для переліку — мінімум полів.
 */
public record GameCardDto(Long id, String title, String platform,
                          BigDecimal price, String coverUrl) {
}
