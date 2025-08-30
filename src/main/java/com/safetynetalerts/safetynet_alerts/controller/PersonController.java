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

import java.util.NoSuchElementException;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/person".
 * Il expose les opérations de création, mise à jour et suppression d'une personne.
 */
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
        try {
            Person created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
        try {
            return service.update(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
    public ResponseEntity<Void> delete(@RequestParam String firstName,
                                       @RequestParam String lastName) {
        try {
            service.delete(firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
