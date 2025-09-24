package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.ChildDTO;
import com.safetynetalerts.safetynet_alerts.service.ChildAlertService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/childAlert".
 * Il permet de récupérer la liste des enfants et des autres membres du foyer vivant à une adresse donnée.
 */
@Slf4j
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
        // --- Requête entrante (INFO)
        log.info("GET /childAlert - request received | address='{}'", address);

        try {
            List<ChildDTO> result = childAlertService.getChildrenByAddress(address);

            // --- Réponse sortante (INFO)
            log.info("GET /childAlert - success | address='{}' | items={}", address, result.size());

            // --- Détails/étapes (DEBUG)
            if (log.isDebugEnabled()) {
                log.debug("GET /childAlert - response payload: {}", result);
            }

            return result;
        } catch (Exception ex) {
            // --- Erreur/exception (ERROR)
            log.error("GET /childAlert - failure | address='{}' | error={}", address, ex.toString(), ex);
            throw ex; // on ne modifie pas le comportement : on re-propage
        }
    }
}

