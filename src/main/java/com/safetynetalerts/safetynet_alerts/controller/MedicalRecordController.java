package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.MedicalRecordCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

/**
 * Contrôleur REST en charge des opérations d'écriture (Create / Update / Delete)
 */
@RestController
@RequestMapping("/medicalRecord")
@RequiredArgsConstructor
public class MedicalRecordController {

    /** Couche service contenant la logique métier pour les dossiers médicaux. */
    private final MedicalRecordService service;

    /**
     * Crée un nouveau {@link MedicalRecord}.
     *
     * @param dto données validées nécessaires à la création du dossier médical
     * @return une réponse 201 Created
     * @throws ResponseStatusException 409 Conflict si la création viole une règle métier
     *                                 (ex. dossier déjà existant pour cette personne)
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> create(@Valid @RequestBody MedicalRecordCreateUpdateDTO dto) {
        try {
            MedicalRecord created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Met à jour un {@link MedicalRecord} existant.
     *
     * @param dto données validées pour la mise à jour
     * @return l'entité mise à jour statut 200 OK
     * @throws ResponseStatusException 404 Not Found si la ressource cible n'existe pas
     */
    @PutMapping
    public MedicalRecord update(@Valid @RequestBody MedicalRecordCreateUpdateDTO dto) {
        try {
            return service.update(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Supprime le {@link MedicalRecord} d'une personne.
     *
     * @param firstName prénom de la personne dont on supprime le dossier (obligatoire)
     * @param lastName  nom de la personne dont on supprime le dossier (obligatoire)
     * @return une réponse 204 No Content si la suppression a bien eu lieu
     * @throws ResponseStatusException 404 Not Found si aucun dossier correspondant n'est trouvé
     */
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        try {
            service.delete(firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
