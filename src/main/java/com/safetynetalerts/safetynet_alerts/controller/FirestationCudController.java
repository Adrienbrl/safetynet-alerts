package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.service.FirestationCudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

/**
 * Contrôleur REST responsable des opérations d'écriture (Create/Update/Delete)
 */
@Slf4j
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
        log.info("POST /firestation - request received | payload={}", dto);
        try {
            Firestation created = service.create(dto);
            log.info("POST /firestation - success | address='{}' | station={}",
                    created.getAddress(), created.getStation());
            if (log.isDebugEnabled()) {
                log.debug("POST /firestation - response payload: {}", created);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            log.error("POST /firestation - conflict | reason={}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception ex) {
            log.error("POST /firestation - failure | error={}", ex.toString(), ex);
            throw ex;
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
        log.info("PUT /firestation - request received | payload={}", dto);
        try {
            Firestation updated = service.update(dto);
            log.info("PUT /firestation - success | address='{}' | station={}",
                    updated.getAddress(), updated.getStation());
            if (log.isDebugEnabled()) {
                log.debug("PUT /firestation - response payload: {}", updated);
            }
            return updated;
        } catch (NoSuchElementException e) {
            log.error("PUT /firestation - not found | reason={}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception ex) {
            log.error("PUT /firestation - failure | error={}", ex.toString(), ex);
            throw ex;
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

        log.info("DELETE /firestation - request received | address='{}' | station={}",
                address, station);

        // validation des paramètres exclusifs
        if ((address == null && station == null) || (address != null && station != null)) {
            log.error("DELETE /firestation - bad request | reason=provide_either_address_or_station");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide either 'address' or 'station'");
        }

        try {
            if (address != null) {
                service.deleteByAddress(address);
                log.info("DELETE /firestation - success | address='{}'", address);
            } else {
                service.deleteByStation(station);
                log.info("DELETE /firestation - success | station={}", station);
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.error("DELETE /firestation - not found | address='{}' | station={} | reason={}",
                    address, station, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception ex) {
            log.error("DELETE /firestation - failure | address='{}' | station={} | error={}",
                    address, station, ex.toString(), ex);
            throw ex;
        }
    }
}
