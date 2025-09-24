package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.FloodStationsResponseDTO;
import com.safetynetalerts.safetynet_alerts.service.FloodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/flood/stations".
 * Il permet de récupérer les foyers (households) couverts par une ou plusieurs casernes.
 */
@Slf4j
@RestController
public class FloodController {

    // Service métier responsable de la logique liée à la fonctionnalité « flood »
    private final FloodService floodService;

    /**
     * Constructeur avec injection du service FloodService.
     *
     * @param floodService service gérant l'agrégation des foyers par station
     */
    public FloodController(FloodService floodService) {
        this.floodService = floodService;
    }

    /**
     * Récupère les foyers (households) pour une liste d'identifiants de casernes.
     *
     * @param stations liste des identifiants de casernes
     * @return 200 avec {@link FloodStationsResponseDTO}, 400 si la liste est absente ou vide
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<FloodStationsResponseDTO> getHouseholdsByStations(
            @RequestParam("stations") List<Integer> stations) {

        // --- Requête entrante (INFO)
        log.info("GET /flood/stations - request received | stations={}", stations);

        // 400 si la liste est absente ou vide
        if (stations == null || stations.isEmpty()) {
            log.error("GET /flood/stations - bad request | reason=missing_or_empty_stations");
            return ResponseEntity.badRequest().build();
        }

        try {
            FloodStationsResponseDTO response = floodService.getHouseholdsByStations(stations);

            // --- Réponse sortante (INFO)
            log.info("GET /flood/stations - success | stations={}", stations);

            // --- Détails (DEBUG)
            if (log.isDebugEnabled()) {
                log.debug("GET /flood/stations - response payload: {}", response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // --- Erreur/exception (ERROR)
            log.error("GET /flood/stations - failure | stations={} | error={}", stations, ex.toString(), ex);
            throw ex; // re-propage : pas de changement de comportement
        }
    }
}

