package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCoverageDTO;
import com.safetynetalerts.safetynet_alerts.service.FirestationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/firestation".
 * Il permet de récupérer la liste des personnes couvertes par une caserne donnée ainsi que le nombre d'adultes et d'enfants.
 */
@RestController
@RequestMapping("/firestation")
public class FirestationController {

    // Service métier responsable de la logique liée aux casernes de pompiers
    private final FirestationService firestationService;

    /**
     * Constructeur avec injection du service FirestationService.
     *
     * @param firestationService service gérant les données relatives aux casernes.
     */
    public FirestationController(FirestationService firestationService) {
        this.firestationService = firestationService;
    }

    /**
     * Récupère la liste des personnes couvertes par une caserne spécifique avec le nombre d'adultes et d'enfants.
     *
     * @param stationNumber numéro de la caserne
     * @return un {@link FirestationCoverageDTO} contenant la liste des personnes
     * couvertes et les statistiques adultes/enfants.
     */
    @GetMapping
    public FirestationCoverageDTO getPersonsByStation(@RequestParam int stationNumber) {
        // Appel au service pour obtenir les personnes couvertes par la caserne
        return firestationService.getPersonsCoveredByStation(stationNumber);
    }
}


