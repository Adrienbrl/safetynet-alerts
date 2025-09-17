package com.safetynetalerts.safetynet_alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FirestationCudControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    // ---------- POST /firestation (create) ----------

    @Test
    @DisplayName("POST /firestation -> 201 Created avec corps Firestation")
    void create_201_whenValid() throws Exception {
        var body = """
      {"address":"999 Test Ave","station":7}
      """;
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address", is("999 Test Ave")))
                .andExpect(jsonPath("$.station", is(7)));
    }


    @Test
    @DisplayName("POST /firestation -> 409 Conflict si doublon")
    void create_409_whenDuplicate() throws Exception {
        var body = """
      {"address":"Dup Addr","station":4}
      """;
        // 1ère création OK
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Doublon -> 409
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    // ---------- PUT /firestation (update) ----------

    @Test
    @DisplayName("PUT /firestation -> 200 et station mise à jour")
    void update_200_whenExisting() throws Exception {
        // Arrange: créer d'abord
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"123 Up St","station":2}
                                """))
                .andExpect(status().isCreated());

        // Act: update
        mvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"123 Up St","station":5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("123 Up St")))
                .andExpect(jsonPath("$.station", is(5)));
    }

    @Test
    @DisplayName("PUT /firestation -> 404 si ressource absente")
    void update_404_whenNotFound() throws Exception {
        mvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"No Such Addr","station":9}
                                """))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE /firestation (delete) ----------

    @Test
    @DisplayName("DELETE /firestation?address=... -> 204 No Content")
    void deleteByAddress_204_whenExisting() throws Exception {
        // créer puis supprimer
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"Del Addr","station":6}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(delete("/firestation").param("address", "Del Addr"))
                .andExpect(status().isNoContent());

        // deuxième suppression -> 404
        mvc.perform(delete("/firestation").param("address", "Del Addr"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /firestation?station=... -> 204 (supprime toutes les entrées de la caserne)")
    void deleteByStation_204_whenExisting() throws Exception {
        // créer 2 entrées pour la même caserne
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"A1","station":777}
                                """))
                .andExpect(status().isCreated());
        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"A2","station":777}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(delete("/firestation").param("station", "777"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /firestation -> 400 si aucun ou les deux paramètres fournis")
    void delete_400_whenBadParams() throws Exception {
        // ni address ni station
        mvc.perform(delete("/firestation"))
                .andExpect(status().isBadRequest());

        // les deux à la fois
        mvc.perform(delete("/firestation")
                        .param("address", "X")
                        .param("station", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /firestation -> 404 si cible inexistante")
    void delete_404_whenTargetNotFound() throws Exception {
        mvc.perform(delete("/firestation").param("address", "Unknown"))
                .andExpect(status().isNotFound());
    }
}

