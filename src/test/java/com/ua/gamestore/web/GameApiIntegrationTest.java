package com.ua.gamestore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.gamestore.repository.GameRepository;
import com.ua.gamestore.web.dto.GameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * END-TO-END тест: {@code @SpringBootTest} піднімає весь застосунок із РЕАЛЬНИМИ бінами
 * (контролер → сервіс → маппер → репозиторій). Нічого не мокається.
 *
 * Тести автономні:
 *  1) перед кожним тестом сховище очищається ({@code deleteAll});
 *  2) кожен тест сам створює свої дані через API ({@link #givenGame}) і отримує їхні реальні id;
 *  3) тест перевіряє лише те, що створив сам, — не стартові ігри з GameSeeder і не чужі дані.
 * Тому порядок запуску тестів не має значення.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GameApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameRepository repository;

    @BeforeEach
    void clearState() {
        repository.deleteAll();
    }

    /** Створює гру через POST і повертає її id, призначений сервером. */
    private long givenGame(String title, String genre) throws Exception {
        GameRequest request = new GameRequest(title, "Тестова студія", new BigDecimal("100.00"),
                2020, "PC", genre, 12, "/covers/test.jpg", "Опис");
        String body = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void getAll_returnsExactlyWhatTestCreated() throws Exception {
        givenGame("Гра A", "Шутер");
        givenGame("Гра B", "Стратегія");

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAll_whenEmpty_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_withGenre_returnsOnlyThatGenreIgnoringCase() throws Exception {
        givenGame("Шутер 1", "Шутер");
        givenGame("Шутер 2", "Шутер");
        givenGame("Стратегія", "Стратегія");

        mockMvc.perform(get("/api/games").param("genre", "шутер"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_returnsDetails() throws Exception {
        long id = givenGame("Hollow Knight", "Метроїдванія");

        mockMvc.perform(get("/api/games/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Hollow Knight"))
                .andExpect(jsonPath("$.genre").value("Метроїдванія"));
    }

    @Test
    void getById_missing_returns404WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/games/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Гру з id=9999 не знайдено"));
    }

    @Test
    void fullCycle_createReadUpdateDelete() throws Exception {
        long id = givenGame("Стара назва", "Шутер");

        GameRequest update = new GameRequest("Нова назва", "Тестова студія", new BigDecimal("150.00"),
                2021, "PlayStation 5", "Шутер", 16, "/covers/test.jpg", "Новий опис");
        mockMvc.perform(put("/api/games/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/api/games/" + id))
                .andExpect(jsonPath("$.title").value("Нова назва"))
                .andExpect(jsonPath("$.platform").value("PlayStation 5"));

        mockMvc.perform(delete("/api/games/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/games/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_invalid_returns400AndSavesNothing() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "title": "", "developer": "X", "price": -5,
                                  "platform": "PC", "genre": "" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());

        mockMvc.perform(get("/api/games"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void delete_missing_returns404() throws Exception {
        mockMvc.perform(delete("/api/games/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
