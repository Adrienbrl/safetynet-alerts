package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.service.FirestationCudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

/**
 * Contrôleur REST responsable des opérations d'écriture (Create/Update/Delete)
 */
@RestController
@RequestMapping("/firestation")
@RequiredArgsConstructor
public class FirestationCudController {

    /** Couche service réalisant la logique métier et l'accès aux données. */
    private final FirestationCudService service;

    /**
     * Crée une nouvelle association {@link Firestation}.
     *
     * @param dto données d'entrée validées pour créer l'association
     * @return une réponse 201 Created
     * @throws ResponseStatusException 409 Conflict si la création viole une contrainte métier (ex. doublon)
     *
     */
    @PostMapping
    public ResponseEntity<Firestation> create(@Valid @RequestBody FirestationCreateUpdateDTO dto) {
        try {
            Firestation created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Met à jour une association {@link Firestation} existante.
     *
     * @param dto données d'entrée validées pour la mise à jour
     * @return l'entité mise à jour avec un statut 200 OK
     * @throws ResponseStatusException 404 Not Found si la ressource à mettre à jour n'existe pas
     */
    @PutMapping
    public Firestation update(@Valid @RequestBody FirestationCreateUpdateDTO dto) {
        try {
            return service.update(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Supprime une association {@link Firestation}.
     *
     * @param address adresse cible pour la suppression (optionnel, exclusif avec {@code station})
     * @param station numéro de station cible pour la suppression (optionnel, exclusif avec {@code address})
     * @return une réponse 204 No Content si la suppression a réussi
     * @throws ResponseStatusException 400 Bad Request si les paramètres sont absents ou concurrents,
     *                                 404 Not Found si aucune ressource correspondante n'a été trouvée
     */
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer station) {

        if ((address == null && station == null) || (address != null && station != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide either 'address' or 'station'");
        }

        try {
            if (address != null) {
                service.deleteByAddress(address);
            } else {
                service.deleteByStation(station);
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
