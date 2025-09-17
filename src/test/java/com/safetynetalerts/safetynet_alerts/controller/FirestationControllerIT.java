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
class FirestationControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("GET /firestation -> 400 si 'stationNumber' est manquant")
    void firestation_400_whenParamMissing() throws Exception {
        mvc.perform(get("/firestation"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /firestation -> 400 si 'stationNumber' n'est pas un entier")
    void firestation_400_whenParamNotInteger() throws Exception {
        mvc.perform(get("/firestation").param("stationNumber", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /firestation -> 200 + JSON {coveredPersons[], numberOfAdults, numberOfChildren}")
    void firestation_200_andResponseShape_whenValidStation() throws Exception {
        // choisis un numéro existant dans ton dataset (1 est souvent présent)
        mvc.perform(get("/firestation").param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.coveredPersons").isArray())
                .andExpect(jsonPath("$.numberOfAdults", isA(Integer.class)))
                .andExpect(jsonPath("$.numberOfChildren", isA(Integer.class)));
    }

    @Test
    @DisplayName("GET /firestation -> 200 et résultat vide si la caserne est inconnue")
    void firestation_200_empty_whenStationUnknown() throws Exception {
        mvc.perform(get("/firestation").param("stationNumber", "999"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.coveredPersons", hasSize(0)))
                .andExpect(jsonPath("$.numberOfAdults", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.numberOfChildren", greaterThanOrEqualTo(0)));
    }
}
