package com.ua.gamestore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.gamestore.exception.GameNotFoundException;
import com.ua.gamestore.service.GameService;
import com.ua.gamestore.web.dto.GameCardDto;
import com.ua.gamestore.web.dto.GameDetailDto;
import com.ua.gamestore.web.dto.GameRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * INTEGRATION-тест веб-шару (зріз): {@code @WebMvcTest} піднімає лише контролер
 * і GlobalExceptionHandler, сервіс замокано через {@code @MockBean}.
 * Перевіряємо HTTP: маршрути, коди відповіді, JSON, валідацію та формат помилок.
 */
@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    private GameDetailDto sampleDetail(long id) {
        return new GameDetailDto(id, "Hollow Knight", "Team Cherry", new BigDecimal("349.00"),
                2017, "Nintendo Switch", "Метроїдванія", 7, "/covers/hollow-knight.jpg", "Опис");
    }

    private String validBody() throws Exception {
        return objectMapper.writeValueAsString(new GameRequest("Hollow Knight", "Team Cherry",
                new BigDecimal("349.00"), 2017, "Nintendo Switch", "Метроїдванія", 7,
                "/covers/hollow-knight.jpg", "Опис"));
    }

    private static final String INVALID_BODY = """
            { "title": "", "developer": "Team Cherry", "price": -10,
              "platform": "PC", "genre": "", "ageRating": 21 }
            """;

    // ---------- GET ----------

    @Test
    void list_returnsCards() throws Exception {
        when(gameService.findAll(null)).thenReturn(List.of(
                new GameCardDto(1L, "Minecraft", "Xbox Series X|S", new BigDecimal("799.00"), "/covers/minecraft.jpg")));

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Minecraft"))
                .andExpect(jsonPath("$[0].platform").value("Xbox Series X|S"));
    }

    @Test
    void list_withGenre_passesFilterToService() throws Exception {
        when(gameService.findAll("Шутер")).thenReturn(List.of());

        mockMvc.perform(get("/api/games").param("genre", "Шутер"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(gameService).findAll("Шутер");
    }

    @Test
    void getOne_returnsDetails() throws Exception {
        when(gameService.findById(6L)).thenReturn(sampleDetail(6L));

        mockMvc.perform(get("/api/games/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.developer").value("Team Cherry"))
                .andExpect(jsonPath("$.ageRating").value(7));
    }

    @Test
    void getOne_missing_returns404WithErrorBody() throws Exception {
        when(gameService.findById(999L)).thenThrow(new GameNotFoundException(999L));

        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Гру з id=999 не знайдено"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    void getOne_notANumber_returns400WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/games/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
        verifyNoInteractions(gameService);
    }

    // ---------- POST ----------

    @Test
    void create_valid_returns201() throws Exception {
        when(gameService.create(any(GameRequest.class))).thenReturn(sampleDetail(6L));

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.title").value("Hollow Knight"));
    }

    @Test
    void create_invalid_returns400WithDetailsAndDoesNotCallService() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Помилка валідації вхідних даних"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(4))
                .andExpect(jsonPath("$.details", hasItems(
                        "title: назва обов'язкова",
                        "price: ціна має бути додатною")));
        verifyNoInteractions(gameService);
    }

    @Test
    void create_brokenJson_returns400WithErrorBody() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Hollow Knight\", \"price\": \"дешево\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Некоректне тіло запиту: очікується JSON гри"));
        verifyNoInteractions(gameService);
    }

    // ---------- PUT ----------

    @Test
    void update_returns200() throws Exception {
        when(gameService.update(eq(2L), any(GameRequest.class))).thenReturn(sampleDetail(2L));

        mockMvc.perform(put("/api/games/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void update_missing_returns404WithErrorBody() throws Exception {
        when(gameService.update(eq(999L), any(GameRequest.class)))
                .thenThrow(new GameNotFoundException(999L));

        mockMvc.perform(put("/api/games/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_invalid_returns400() throws Exception {
        mockMvc.perform(put("/api/games/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray());
        verifyNoInteractions(gameService);
    }

    // ---------- DELETE ----------

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/games/1"))
                .andExpect(status().isNoContent());
        verify(gameService).delete(1L);
    }

    @Test
    void delete_missing_returns404WithErrorBody() throws Exception {
        doThrow(new GameNotFoundException(999L)).when(gameService).delete(999L);

        mockMvc.perform(delete("/api/games/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Гру з id=999 не знайдено"));
    }
}
