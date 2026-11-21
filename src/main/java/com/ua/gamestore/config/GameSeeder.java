package com.ua.gamestore.config;

import com.ua.gamestore.domain.Game;
import com.ua.gamestore.repository.GameRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Початкове наповнення сховища іграми під час запуску.
 * Поки немає POST-ендпоінта, нові ігри додаються саме тут.
 */
@Component
public class GameSeeder implements ApplicationRunner {

    private final GameRepository repository;

    public GameSeeder(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        repository.save(new Game(null, "S.T.A.L.K.E.R. 2: Heart of Chornobyl", "GSC Game World",
                new BigDecimal("1299.00"), 2024, "PC", "Шутер", 18,
                "/covers/stalker-2.jpg",
                "Шутер з елементами виживання у відкритому світі Чорнобильської зони відчуження."));

        repository.save(new Game(null, "Metro Exodus", "4A Games",
                new BigDecimal("649.00"), 2019, "PlayStation 5", "Шутер", 18,
                "/covers/metro-exodus.jpg",
                "Подорож на потязі «Аврора» крізь постапокаліптичний світ після ядерної війни."));

        repository.save(new Game(null, "The Witcher 3: Wild Hunt", "CD Projekt Red",
                new BigDecimal("499.00"), 2015, "Nintendo Switch", "Рольова гра", 18,
                "/covers/witcher-3.jpg",
                "Мисливець на чудовиськ Геральт шукає свою названу доньку Цірі у великому відкритому світі."));

        repository.save(new Game(null, "Minecraft", "Mojang Studios",
                new BigDecimal("799.00"), 2011, "Xbox Series X|S", "Пісочниця", 7,
                "/covers/minecraft.jpg",
                "Світ із блоків, у якому можна будувати, досліджувати печери та виживати."));

        repository.save(new Game(null, "Portal 2", "Valve",
                new BigDecimal("249.00"), 2011, "PC", "Головоломка", 12,
                "/covers/portal-2.jpg",
                "Головоломки з порталами в лабораторії Aperture Science, є режим для двох гравців."));
    }
}
