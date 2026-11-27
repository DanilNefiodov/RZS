package com.ua.gamestore.service.mapper;

import com.ua.gamestore.domain.Game;
import com.ua.gamestore.web.dto.GameCardDto;
import com.ua.gamestore.web.dto.GameDetailDto;
import com.ua.gamestore.web.dto.GameRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UNIT-тест маппера: чиста Java, без Spring і без моків.
 */
class GameMapperTest {

    private final GameMapper mapper = new GameMapper();

    private Game sampleGame() {
        return new Game(3L, "The Witcher 3: Wild Hunt", "CD Projekt Red",
                new BigDecimal("499.00"), 2015, "Nintendo Switch", "Рольова гра", 18,
                "/covers/witcher-3.jpg", "Опис");
    }

    @Test
    void toCard_keepsOnlyCardFields() {
        GameCardDto card = mapper.toCard(sampleGame());

        assertThat(card).isEqualTo(new GameCardDto(3L, "The Witcher 3: Wild Hunt",
                "Nintendo Switch", new BigDecimal("499.00"), "/covers/witcher-3.jpg"));
    }

    @Test
    void toDetail_copiesAllFields() {
        GameDetailDto detail = mapper.toDetail(sampleGame());

        assertThat(detail.id()).isEqualTo(3L);
        assertThat(detail.developer()).isEqualTo("CD Projekt Red");
        assertThat(detail.releaseYear()).isEqualTo(2015);
        assertThat(detail.genre()).isEqualTo("Рольова гра");
        assertThat(detail.ageRating()).isEqualTo(18);
        assertThat(detail.description()).isEqualTo("Опис");
    }

    @Test
    void toDomain_copiesFieldsAndLeavesIdEmpty() {
        GameRequest request = new GameRequest("Portal 2", "Valve", new BigDecimal("249.00"),
                2011, "PC", "Головоломка", 12, "/covers/portal-2.jpg", "Опис");

        Game game = mapper.toDomain(request);

        assertThat(game.getId()).isNull();
        assertThat(game.getTitle()).isEqualTo("Portal 2");
        assertThat(game.getPlatform()).isEqualTo("PC");
        assertThat(game.getAgeRating()).isEqualTo(12);
    }
}
