package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.FireAddressResponseDTO;
import com.safetynetalerts.safetynet_alerts.service.FireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/fire".
 * Il permet de récupérer les informations liées à une adresse : personnes concernées
 * et caserne couvrant la zone.
 */
@Slf4j
@RestController
@RequestMapping(value = "/fire", produces = MediaType.APPLICATION_JSON_VALUE)
public class FireController {

    // Service métier responsable de la logique liée aux informations « fire » par adresse
    private final FireService fireService;

    /**
     * Constructeur avec injection du service FireService.
     *
     * @param fireService service gérant la récupération des informations « fire »
     */
    public FireController(FireService fireService) {
        this.fireService = fireService;
    }

    /**
     * Récupère les informations « fire » pour une adresse donnée.
     *
     * @param address adresse à rechercher
     * @return 200 avec {@link FireAddressResponseDTO} si trouvé,
     *         400 si le paramètre est manquant ou vide,
     *         404 si aucune couverture n'est trouvée
     */
    @GetMapping
    public ResponseEntity<?> getFireByAddress(@RequestParam("address") String address) {
        // --- Requête entrante (INFO)
        log.info("GET /fire - request received | address='{}'", address);

        // 400 si paramètre manquant ou vide
        if (address == null || address.isBlank()) {
            log.error("GET /fire - bad request | reason=missing_or_blank_address");
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", 400,
                            "error", "Bad Request",
                            "message", "Query param 'address' is required"
                    ));
        }

        try {
            // Appel service + mapping réponse
            return fireService.getFireInfoByAddress(address)
                    .<ResponseEntity<?>>map(dto -> {
                        // --- Réponse réussie (INFO)
                        log.info("GET /fire - success | address='{}'", address);
                        // --- Détails (DEBUG)
                        if (log.isDebugEnabled()) {
                            log.debug("GET /fire - response payload: {}", dto);
                        }
                        return ResponseEntity.ok(dto);
                    })
                    .orElseGet(() -> {
                        // 404 si aucune couverture
                        log.error("GET /fire - not found | address='{}'", address);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "status", 404,
                                        "error", "Not Found",
                                        "message", "No fire station coverage found for address: " + address.trim()
                                ));
                    });
        } catch (Exception ex) {
            // --- Exception (ERROR) + re-propagation
            log.error("GET /fire - failure | address='{}' | error={}", address, ex.toString(), ex);
            throw ex;
        }
    }

}

