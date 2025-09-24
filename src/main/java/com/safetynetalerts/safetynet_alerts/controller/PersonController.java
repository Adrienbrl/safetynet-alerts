package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.PersonCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/person".
 * Il expose les opérations de création, mise à jour et suppression d'une personne.
 */
@Slf4j
@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
@Validated
public class PersonController {

    // Service métier gérant les opérations sur la ressource Person
    private final PersonService service;

    /**
     * Crée une nouvelle personne.
     *
     * @param dto payload de création
     * @return 201 avec l'entité créée
     * @throws ResponseStatusException 409 en cas de conflit (ex. doublon)
     */
    @PostMapping
    public ResponseEntity<Person> create(@Valid @RequestBody PersonCreateUpdateDTO dto) {
        log.info("POST /person - request received | firstName='{}' | lastName='{}'",
                dto.getFirstName(), dto.getLastName());
        try {
            Person created = service.create(dto);
            log.info("POST /person - success | firstName='{}' | lastName='{}'",
                    created.getFirstName(), created.getLastName());
            if (log.isDebugEnabled()) {
                log.debug("POST /person - response payload: {}", created);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            log.error("POST /person - conflict | firstName='{}' | lastName='{}' | reason={}",
                    dto.getFirstName(), dto.getLastName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception ex) {
            log.error("POST /person - failure | firstName='{}' | lastName='{}' | error={}",
                    dto.getFirstName(), dto.getLastName(), ex.toString(), ex);
            throw ex;
        }
    }

    /**
     * Met à jour une personne existante.
     *
     * @param dto payload de mise à jour
     * @return l'entité mise à jour
     * @throws ResponseStatusException 404 si la personne n'existe pas
     */
    @PutMapping
    public Person update(@Valid @RequestBody PersonCreateUpdateDTO dto) {
        log.info("PUT /person - request received | firstName='{}' | lastName='{}'",
                dto.getFirstName(), dto.getLastName());
        try {
            Person updated = service.update(dto);
            log.info("PUT /person - success | firstName='{}' | lastName='{}'",
                    updated.getFirstName(), updated.getLastName());
            if (log.isDebugEnabled()) {
                log.debug("PUT /person - response payload: {}", updated);
            }
            return updated;
        } catch (NoSuchElementException e) {
            log.error("PUT /person - not found | firstName='{}' | lastName='{}' | reason={}",
                    dto.getFirstName(), dto.getLastName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception ex) {
            log.error("PUT /person - failure | firstName='{}' | lastName='{}' | error={}",
                    dto.getFirstName(), dto.getLastName(), ex.toString(), ex);
            throw ex;
        }
    }

    /**
     * Supprime une personne à partir du prénom et du nom.
     *
     * @param firstName prénom de la personne
     * @param lastName  nom de la personne
     * @return 204 si la suppression a réussi
     * @throws ResponseStatusException 404 si la personne n'existe pas
     */
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {

        log.info("DELETE /person - request received | firstName='{}' | lastName='{}'",
                firstName, lastName);

        try {
            service.delete(firstName, lastName);
            log.info("DELETE /person - success | firstName='{}' | lastName='{}'",
                    firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.error("DELETE /person - not found | firstName='{}' | lastName='{}' | reason={}",
                    firstName, lastName, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception ex) {
            log.error("DELETE /person - failure | firstName='{}' | lastName='{}' | error={}",
                    firstName, lastName, ex.toString(), ex);
            throw ex;
        }
    }
}
