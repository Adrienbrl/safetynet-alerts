package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.service.PhoneAlertService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/phoneAlert".
 * Il permet de récupérer les numéros de téléphone des habitants couverts par une caserne donnée.
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PhoneAlertController {

    // Service métier responsable de la logique liée aux alertes téléphoniques
    private final PhoneAlertService phoneAlertService;

    /**
     * Constructeur avec injection du service PhoneAlertService.
     *
     * @param phoneAlertService service gérant les données relatives aux numéros de téléphone.
     */
    public PhoneAlertController(PhoneAlertService phoneAlertService) {
        this.phoneAlertService = phoneAlertService;
    }

    /**
     * Récupère la liste des numéros de téléphone des habitants couverts par une caserne spécifique.
     *
     * @param station numéro de la caserne
     * @return une {@link ResponseEntity} contenant la liste des numéros de téléphone,
     * ou une erreur 400 si le numéro de caserne est invalide.
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> phoneAlert(@RequestParam("firestation") int station) {
        // --- Requête entrante (INFO)
        log.info("GET /phoneAlert - request received | station={}", station);

        // 400 si station invalide
        if (station <= 0) {
            log.error("GET /phoneAlert - bad request | reason=invalid_station (<=0)");
            return ResponseEntity.badRequest().build();
        }

        try {
            // Appel service
            List<String> phones = phoneAlertService.getPhonesByStation(station);

            // --- Réponse sortante (INFO)
            log.info("GET /phoneAlert - success | station={} | items={}", station, phones.size());

            // --- Détails (DEBUG)
            if (log.isDebugEnabled()) {
                log.debug("GET /phoneAlert - response payload: {}", phones);
            }

            return ResponseEntity.ok(phones);
        } catch (Exception ex) {
            // --- Erreur/exception (ERROR)
            log.error("GET /phoneAlert - failure | station={} | error={}", station, ex.toString(), ex);
            throw ex; // re-propage : aucun changement de comportement
        }
    }
}

