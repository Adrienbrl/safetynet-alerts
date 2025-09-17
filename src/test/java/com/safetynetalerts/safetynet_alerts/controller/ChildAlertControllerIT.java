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
class ChildAlertControllerIT {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("GET /childAlert -> 400 si le paramètre 'address' est manquant")
    void childAlert_returns400_whenParamMissing() throws Exception {
        mvc.perform(get("/childAlert"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /childAlert -> 200 JSON (liste) quand une adresse est fournie")
    void childAlert_returns200_andJsonArray_whenAddressProvided() throws Exception {
        mvc.perform(get("/childAlert").param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // La réponse est toujours une liste (éventuellement vide)
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /childAlert -> 200 et liste vide si l'adresse est inconnue")
    void childAlert_returnsEmptyArray_whenAddressUnknown() throws Exception {
        mvc.perform(get("/childAlert").param("address", "Unknown Address 123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /childAlert -> 200 et liste vide si l'adresse est vide (espaces)")
    void childAlert_returnsEmptyArray_whenAddressBlank() throws Exception {
        mvc.perform(get("/childAlert").param("address", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
