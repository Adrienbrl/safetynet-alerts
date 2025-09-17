package com.safetynetalerts.safetynet_alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class CommunityEmailControllerIT {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    @DisplayName("400 si 'city' est manquant")
    void communityEmail_400_whenParamMissing() throws Exception {
        mvc.perform(get("/communityEmail"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("200 + JSON array quand 'city' est fourni")
    void communityEmail_200_andJsonArray() throws Exception {
        mvc.perform(get("/communityEmail").param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("200 + [] quand la ville est inconnue")
    void communityEmail_200_empty_whenCityUnknown() throws Exception {
        mvc.perform(get("/communityEmail").param("city", "Nowhere City"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("200 + [] quand la ville est vide (espaces)")
    void communityEmail_200_empty_whenCityBlank() throws Exception {
        mvc.perform(get("/communityEmail").param("city", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}