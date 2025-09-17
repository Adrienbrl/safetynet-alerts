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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PersonControllerIT {

    @Autowired MockMvc mvc;

    private static final String BASE = "/person";

    // -------- POST /person --------

    @Test
    @DisplayName("POST /person -> 201 Created (payload valide)")
    void create_201_whenValid() throws Exception {
        var body = """
    {
      "firstName":"John",
      "lastName":"Doe",
      "address":"1509 Culver St",
      "city":"Culver",
      "zip":"97451",
      "phone":"841-874-6512",
      "email":"john.doe@test.com"
    }
    """;
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    @DisplayName("POST /person -> 400 si champs requis manquants / vides")
    void create_400_whenMissingOrBlank() throws Exception {
        var body = """
    {
      "firstName":"",                // blank
      "lastName":"Doe",
      "address":"1509 Culver St",
      "city":"Culver",
      "zip":"97451",
      "phone":"841-874-6512",
      "email":"john.doe@test.com"
    }
    """;
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /person -> 409 Conflict en cas de doublon")
    void create_409_whenDuplicate() throws Exception {
        var body = """
    {
      "firstName":"Dup",
      "lastName":"User",
      "address":"A",
      "city":"C",
      "zip":"Z",
      "phone":"000-000-0000",
      "email":"dup@user.test"
    }
    """;
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    // -------- PUT /person --------

    @Test
    @DisplayName("PUT /person -> 200 OK quand la personne existe")
    void update_200_whenExisting() throws Exception {
        // create
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Ann","lastName":"Lee","address":"A","city":"C","zip":"Z","phone":"1","email":"ann@x.test"}
    """)).andExpect(status().isCreated());

        // update
        mvc.perform(put(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Ann","lastName":"Lee","address":"New Addr","city":"New City","zip":"99999","phone":"2","email":"ann@x.test"}
    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Ann")))
                .andExpect(jsonPath("$.address", is("New Addr")));
    }

    @Test
    @DisplayName("PUT /person -> 404 si la personne n'existe pas")
    void update_404_whenNotFound() throws Exception {
        mvc.perform(put(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Ghost","lastName":"User","address":"A","city":"C","zip":"Z","phone":"1","email":"ghost@x.test"}
    """)).andExpect(status().isNotFound());
    }

    // -------- DELETE /person --------

    @Test
    @DisplayName("DELETE /person?firstName=&lastName= -> 204 No Content")
    void delete_204_whenExisting() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
    {"firstName":"Del","lastName":"User","address":"A","city":"C","zip":"Z","phone":"1","email":"del@x.test"}
    """)).andExpect(status().isCreated());

        mvc.perform(delete(BASE).param("firstName","Del").param("lastName","User"))
                .andExpect(status().isNoContent());

        // deuxième suppression -> 404
        mvc.perform(delete(BASE).param("firstName","Del").param("lastName","User"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /person -> 404 si la personne est inconnue")
    void delete_404_whenUnknown() throws Exception {
        mvc.perform(delete(BASE).param("firstName","Nobody").param("lastName","Here"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /person -> 400 si un paramètre est manquant")
    void delete_400_whenMissingParam() throws Exception {
        mvc.perform(delete(BASE).param("firstName","OnlyFirst"))
                .andExpect(status().isBadRequest());
    }
}

