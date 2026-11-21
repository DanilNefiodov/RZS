package com.ua.gamestore.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}
