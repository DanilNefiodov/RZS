package com.ua.gamestore.service;

import com.ua.gamestore.web.dto.GameCardDto;
import com.ua.gamestore.web.dto.GameDetailDto;

import java.util.List;

/**
 * Бізнес-операції з іграми.
 *
 * ШАР: service (інтерфейс). Контролер залежить від цієї абстракції (DIP).
 */
public interface GameService {

    List<GameCardDto> findAll();

    GameDetailDto findById(Long id);
}
