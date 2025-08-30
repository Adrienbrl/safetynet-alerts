package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.ChildDTO;
import com.safetynetalerts.safetynet_alerts.service.ChildAlertService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/childAlert".
 * Il permet de récupérer la liste des enfants et des autres membres du foyer vivant à une adresse donnée.
 */
@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    // Service métier responsable de la logique liée aux alertes enfants
    private final ChildAlertService childAlertService;

    /**
     * Constructeur avec injection du service ChildAlertService.
     *
     * @param childAlertService service gérant les données relatives aux enfants.
     */
    public ChildAlertController(ChildAlertService childAlertService) {
        this.childAlertService = childAlertService;
    }

    /**
     * Récupère la liste des enfants habitant à une adresse donnée ainsi que les autres membres du foyer.
     *
     * @param address adresse à rechercher
     * @return liste de {@link ChildDTO} contenant les informations des enfants et des autres membres du foyer.
     */
    @GetMapping
    public List<ChildDTO> getChildrenByAddress(@RequestParam String address) {
        // Appel au service pour obtenir la liste des enfants et autres membres
        return childAlertService.getChildrenByAddress(address);
    }
}

