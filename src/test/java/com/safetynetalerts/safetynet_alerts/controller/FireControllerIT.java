package com.safetynetalerts.safetynet_alerts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FireControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("GET /fire -> 400 si le paramètre 'address' est manquant")
    void fire_400_whenParamMissing() throws Exception {
        mvc.perform(get("/fire").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /fire -> 400 si 'address' est vide ou blancs")
    void fire_400_whenAddressBlank() throws Exception {
        mvc.perform(get("/fire").param("address", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("address")))
                .andExpect(jsonPath("$.message", containsString("required")));
    }

    @Test
    @DisplayName("GET /fire -> 404 si aucune couverture n'est trouvée")
    void fire_404_whenAddressUnknown() throws Exception {
        String unknown = "Unknown Address 123";
        mvc.perform(get("/fire").param("address", unknown))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("No fire station coverage found")))
                .andExpect(jsonPath("$.message", containsString(unknown)));
    }

    @Test
    @DisplayName("GET /fire -> 200 + JSON {stationNumbers[], residents[]} quand l'adresse existe")
    void fire_200_andResponseShape_whenAddressKnown() throws Exception {
        mvc.perform(get("/fire").param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stationNumbers").isArray())
                .andExpect(jsonPath("$.residents").isArray());
    }
}

