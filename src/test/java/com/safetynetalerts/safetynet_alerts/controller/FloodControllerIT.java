package com.safetynetalerts.safetynet_alerts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FloodControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("GET /flood/stations -> 400 si le paramètre 'stations' est manquant")
    void flood_400_whenParamMissing() throws Exception {
        mvc.perform(get("/flood/stations"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /flood/stations -> 400 si un identifiant n'est pas un entier")
    void flood_400_whenNonInteger() throws Exception {
        mvc.perform(get("/flood/stations").param("stations", "1", "abc", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /flood/stations -> 200 + JSON {stations[], households[]} quand la liste est valide")
    void flood_200_andResponseShape() throws Exception {
        // format multi-param recommandé: ?stations=1&stations=2&stations=3
        mvc.perform(get("/flood/stations")
                        .param("stations", "1", "2", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stations").isArray())
                .andExpect(jsonPath("$.households").isArray());
    }
}

