package com.safetynetalerts.safetynet_alerts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MedicalRecordControllerIT {

    @Autowired MockMvc mvc;

    private static final String BASE = "/medicalRecord";

    // ----------- POST /medicalRecord -----------

    @Test
    @DisplayName("POST /medicalRecord -> 201 Created (valid)")
    void create_201_whenValid() throws Exception {
        var body = """
    {
      "firstName":"UT_First",
      "lastName":"UT_Last",
      "birthdate":"03/06/1984",
      "medications":["aznol:350mg","hydrapermazol:100mg"],
      "allergies":["peanut"]
    }
    """;

        mvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("UT_First"))
                .andExpect(jsonPath("$.lastName").value("UT_Last"));
    }

    @Test
    @DisplayName("POST /medicalRecord -> 409 Conflict si doublon")
    void create_409_whenDuplicate() throws Exception {
        var body = """
    {
      "firstName":"Dup",
      "lastName":"User",
      "birthdate":"01/01/1990",
      "medications":[],
      "allergies":[]
    }
    """;
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    // ----------- PUT /medicalRecord -----------

    @Test
    @DisplayName("PUT /medicalRecord -> 200 OK (mise à jour existante)")
    void update_200_whenExisting() throws Exception {
        // create
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Up","lastName":"Me","birthdate":"02/10/1980","medications":[],"allergies":[]}
    """)).andExpect(status().isCreated());

        // update
        mvc.perform(put(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Up","lastName":"Me","birthdate":"02/10/1980","medications":["paracetamol:500mg"],"allergies":["pollen"]}
    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Up"))
                .andExpect(jsonPath("$.lastName").value("Me"));
    }

    @Test
    @DisplayName("PUT /medicalRecord -> 404 si ressource absente")
    void update_404_whenNotFound() throws Exception {
        mvc.perform(put(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Ghost","lastName":"User","birthdate":"01/01/1991","medications":[],"allergies":[]}
    """)).andExpect(status().isNotFound());
    }

    // ----------- DELETE /medicalRecord -----------

    @Test
    @DisplayName("DELETE /medicalRecord?firstName=&lastName= -> 204 No Content")
    void delete_204_whenExisting() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Del","lastName":"User","birthdate":"05/05/1975","medications":[],"allergies":[]}
    """)).andExpect(status().isCreated());

        mvc.perform(delete(BASE).param("firstName","Del").param("lastName","User"))
                .andExpect(status().isNoContent());

        // supprimer encore -> 404
        mvc.perform(delete(BASE).param("firstName","Del").param("lastName","User"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /medicalRecord -> 404 si inconnu")
    void delete_404_whenUnknown() throws Exception {
        mvc.perform(delete(BASE).param("firstName","Nobody").param("lastName","Here"))
                .andExpect(status().isNotFound());
    }
}
