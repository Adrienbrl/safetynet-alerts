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
class PersonInfoControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("GET /personInfo -> 400 si 'lastName' est manquant")
    void personInfo_400_whenParamMissing() throws Exception {
        mvc.perform(get("/personInfo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /personInfo -> 200 + JSON array quand 'lastName' est fourni")
    void personInfo_200_andJsonArray() throws Exception {
        mvc.perform(get("/personInfo").param("lastName", "Boyd")  // utilise un nom existant si dispo
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /personInfo -> 200 et [] si le nom est inconnu")
    void personInfo_200_empty_whenUnknown() throws Exception {
        mvc.perform(get("/personInfo").param("lastName", "NoSuchLastNameXYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /personInfo -> 200 et [] si le nom est vide (espaces)")
    void personInfo_200_empty_whenBlank() throws Exception {
        mvc.perform(get("/personInfo").param("lastName", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /personInfo -> structure d'un PersonInfoDTO")
    void personInfo_validatesDtoShape() throws Exception {
        mvc.perform(get("/personInfo").param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // si la liste est non vide, vérifier la structure du 1er élément
                .andExpect(jsonPath("$[0].firstName", anyOf(nullValue(), isA(String.class))))
                .andExpect(jsonPath("$[0].lastName", anyOf(nullValue(), isA(String.class))))
                .andExpect(jsonPath("$[0].address", anyOf(nullValue(), isA(String.class))))
                .andExpect(jsonPath("$[0].age", anyOf(nullValue(), isA(Integer.class))))
                .andExpect(jsonPath("$[0].email", anyOf(nullValue(), isA(String.class))))
                .andExpect(jsonPath("$[0].medications").exists())
                .andExpect(jsonPath("$[0].allergies").exists());
    }
}
