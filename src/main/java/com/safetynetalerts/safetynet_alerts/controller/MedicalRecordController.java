package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.MedicalRecordCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

/**
 * Contrôleur REST en charge des opérations d'écriture (Create / Update / Delete)
 */
@Slf4j
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
        log.info("POST /medicalRecord - request received | firstName='{}' | lastName='{}'",
                dto.getFirstName(), dto.getLastName());
        try {
            MedicalRecord created = service.create(dto);
            log.info("POST /medicalRecord - success | firstName='{}' | lastName='{}'",
                    created.getFirstName(), created.getLastName());
            if (log.isDebugEnabled()) {
                log.debug("POST /medicalRecord - response entity created (id omitted)");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            log.error("POST /medicalRecord - conflict | firstName='{}' | lastName='{}' | reason={}",
                    dto.getFirstName(), dto.getLastName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception ex) {
            log.error("POST /medicalRecord - failure | firstName='{}' | lastName='{}' | error={}",
                    dto.getFirstName(), dto.getLastName(), ex.toString(), ex);
            throw ex;
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
        log.info("PUT /medicalRecord - request received | firstName='{}' | lastName='{}'",
                dto.getFirstName(), dto.getLastName());
        try {
            MedicalRecord updated = service.update(dto);
            log.info("PUT /medicalRecord - success | firstName='{}' | lastName='{}'",
                    updated.getFirstName(), updated.getLastName());
            if (log.isDebugEnabled()) {
                log.debug("PUT /medicalRecord - response entity updated (details omitted)");
            }
            return updated;
        } catch (NoSuchElementException e) {
            log.error("PUT /medicalRecord - not found | firstName='{}' | lastName='{}' | reason={}",
                    dto.getFirstName(), dto.getLastName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception ex) {
            log.error("PUT /medicalRecord - failure | firstName='{}' | lastName='{}' | error={}",
                    dto.getFirstName(), dto.getLastName(), ex.toString(), ex);
            throw ex;
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

        log.info("DELETE /medicalRecord - request received | firstName='{}' | lastName='{}'",
                firstName, lastName);

        try {
            service.delete(firstName, lastName);
            log.info("DELETE /medicalRecord - success | firstName='{}' | lastName='{}'",
                    firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.error("DELETE /medicalRecord - not found | firstName='{}' | lastName='{}' | reason={}",
                    firstName, lastName, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception ex) {
            log.error("DELETE /medicalRecord - failure | firstName='{}' | lastName='{}' | error={}",
                    firstName, lastName, ex.toString(), ex);
            throw ex;
        }
    }
}
