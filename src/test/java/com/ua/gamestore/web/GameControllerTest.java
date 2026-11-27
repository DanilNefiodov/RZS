package com.ua.gamestore.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Перевіряє всі ендпоінти на справжньому застосунку з початковими іграми з GameSeeder.
 * Після кожного тесту контекст перезапускається, щоб зміни (створення/видалення)
 * не впливали на інші тести.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameControllerTest {

    private static final String GAME_JSON = """
            {
              "title": "Hollow Knight",
              "developer": "Team Cherry",
              "price": 349.00,
              "releaseYear": 2017,
              "platform": "Nintendo Switch",
              "genre": "Метроїдванія",
              "ageRating": 7,
              "coverUrl": "/covers/hollow-knight.jpg",
              "description": "Мандрівка зруйнованим підземним королівством комах."
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    // ---------- GET ----------

    @Test
    void listReturnsSeededGames() throws Exception {
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].platform").exists())
                .andExpect(jsonPath("$[0].description").doesNotExist());
    }

    @Test
    void listFiltersByGenre() throws Exception {
        mockMvc.perform(get("/api/games").param("genre", "шутер"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getOneReturnsDetails() throws Exception {
        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("S.T.A.L.K.E.R. 2: Heart of Chornobyl"))
                .andExpect(jsonPath("$.developer").value("GSC Game World"))
                .andExpect(jsonPath("$.ageRating").value(18));
    }

    @Test
    void getOneUnknownIdReturns404() throws Exception {
        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound());
    }

    // ---------- POST ----------

    @Test
    void createReturns201AndNewId() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.title").value("Hollow Knight"));

        mockMvc.perform(get("/api/games/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.developer").value("Team Cherry"));
    }

    @Test
    void createInvalidReturns400AndCreatesNothing() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "title": "", "developer": "X", "price": -10,
                                  "platform": "PC", "genre": "", "ageRating": 21 }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/games"))
                .andExpect(jsonPath("$.length()").value(5));
    }

    // ---------- PUT ----------

    @Test
    void updateReturns200AndKeepsId() throws Exception {
        mockMvc.perform(put("/api/games/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Hollow Knight"));
    }

    @Test
    void updateUnknownIdReturns404() throws Exception {
        mockMvc.perform(put("/api/games/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_JSON))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE ----------

    @Test
    void deleteReturns204AndGameIsGone() throws Exception {
        mockMvc.perform(delete("/api/games/5"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/games/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUnknownIdReturns404() throws Exception {
        mockMvc.perform(delete("/api/games/999"))
                .andExpect(status().isNotFound());
    }
}
