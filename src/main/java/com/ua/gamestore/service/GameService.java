package com.ua.gamestore.service;

import com.ua.gamestore.web.dto.GameCardDto;
import com.ua.gamestore.web.dto.GameDetailDto;
import com.ua.gamestore.web.dto.GameRequest;

import java.util.List;

/**
 * Бізнес-операції з іграми.
 *
 * ШАР: service (інтерфейс). Контролер залежить від цієї абстракції (DIP).
 */
public interface GameService {

    /** Перелік карток; якщо genre задано — лише ігри цього жанру. */
    List<GameCardDto> findAll(String genre);

    /** Одна гра з деталями; кидає GameNotFoundException, якщо немає. */
    GameDetailDto findById(Long id);

    /** Створити гру; повертає створену з призначеним id. */
    GameDetailDto create(GameRequest request);

    /** Оновити гру за id; кидає GameNotFoundException, якщо немає. */
    GameDetailDto update(Long id, GameRequest request);

    /** Видалити гру за id; кидає GameNotFoundException, якщо немає. */
    void delete(Long id);
}
