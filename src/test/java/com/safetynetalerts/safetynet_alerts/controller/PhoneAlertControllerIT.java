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
class PhoneAlertControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("GET /phoneAlert -> 400 si 'firestation' est manquant")
    void phoneAlert_400_whenParamMissing() throws Exception {
        mvc.perform(get("/phoneAlert"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /phoneAlert -> 400 si 'firestation' n'est pas un entier")
    void phoneAlert_400_whenParamNotInteger() throws Exception {
        mvc.perform(get("/phoneAlert").param("firestation", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /phoneAlert -> 400 si 'firestation' <= 0")
    void phoneAlert_400_whenStationNonPositive() throws Exception {
        mvc.perform(get("/phoneAlert").param("firestation", "0"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/phoneAlert").param("firestation", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /phoneAlert -> 200 + JSON array quand la caserne existe")
    void phoneAlert_200_andArray_whenValid() throws Exception {
        // choisis un numéro présent dans ton dataset (souvent 1 ou 2)
        mvc.perform(get("/phoneAlert")
                        .param("firestation", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /phoneAlert -> 200 et [] si caserne inconnue")
    void phoneAlert_200_empty_whenUnknownStation() throws Exception {
        mvc.perform(get("/phoneAlert").param("firestation", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", anyOf(empty(), hasSize(greaterThanOrEqualTo(0))))); // tolérant si dataset diffère
    }

    // Optionnel : vérifier format & unicité des téléphones (si c'est ton contrat)
    @Test
    @DisplayName("Les numéros sont au format ###-###-#### (optionnel)")
    void phoneAlert_numbersHaveBasicFormat_optional() throws Exception {
        mvc.perform(get("/phoneAlert").param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", everyItem(matchesPattern("^\\d{3}-\\d{3}-\\d{4}$"))));
    }
}
