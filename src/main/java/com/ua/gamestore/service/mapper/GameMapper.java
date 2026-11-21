package com.ua.gamestore.service.mapper;

import com.ua.gamestore.domain.Game;
import com.ua.gamestore.web.dto.GameCardDto;
import com.ua.gamestore.web.dto.GameDetailDto;
import com.ua.gamestore.web.dto.GameRequest;
import org.springframework.stereotype.Component;

/**
 * Перетворення між доменною {@link Game} і DTO (SRP: лише мапінг).
 */
@Component
public class GameMapper {

    public GameCardDto toCard(Game g) {
        return new GameCardDto(g.getId(), g.getTitle(), g.getPlatform(),
                g.getPrice(), g.getCoverUrl());
    }

    public GameDetailDto toDetail(Game g) {
        return new GameDetailDto(g.getId(), g.getTitle(), g.getDeveloper(), g.getPrice(),
                g.getReleaseYear(), g.getPlatform(), g.getGenre(), g.getAgeRating(),
                g.getCoverUrl(), g.getDescription());
    }

    /**
     * Вхідний DTO → доменний об'єкт.
     * id тут не встановлюємо: при створенні його призначить репозиторій,
     * при оновленні — сервіс зі шляху запиту.
     */
    public Game toDomain(GameRequest r) {
        Game g = new Game();
        g.setTitle(r.title());
        g.setDeveloper(r.developer());
        g.setPrice(r.price());
        g.setReleaseYear(r.releaseYear());
        g.setPlatform(r.platform());
        g.setGenre(r.genre());
        g.setAgeRating(r.ageRating());
        g.setCoverUrl(r.coverUrl());
        g.setDescription(r.description());
        return g;
    }
}
