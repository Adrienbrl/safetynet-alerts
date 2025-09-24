package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.PersonInfoDTO;
import com.safetynetalerts.safetynet_alerts.service.PersonInfoService;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/personInfo".
 * Il permet de récupérer les informations détaillées des personnes portant un même nom de famille.
 */
@Slf4j
@RestController
@RequestMapping(value = "/personInfo", produces = "application/json")
public class PersonInfoController {

    // Service métier responsable de l'agrégation des informations « person info »
    private final PersonInfoService personInfoService;

    /**
     * Constructeur avec injection du service PersonInfoService.
     *
     * @param personInfoService service gérant l'assemblage des informations détaillées.
     */
    public PersonInfoController(PersonInfoService personInfoService) {
        this.personInfoService = personInfoService;
    }

    /**
     * Récupère les informations détaillées pour un nom de famille.
     *
     * @param lastName nom de famille à rechercher
     * @return liste de {@link PersonInfoDTO} (éventuellement vide)
     */
    @GetMapping
    public List<PersonInfoDTO> getPersonInfo(@RequestParam String lastName) {
        // --- Requête entrante (INFO)
        log.info("GET /personInfo - request received | lastName='{}'", lastName);

        try {
            List<PersonInfoDTO> result = personInfoService.getPersonsInfoByLastName(lastName);

            // --- Réponse sortante (INFO)
            log.info("GET /personInfo - success | lastName='{}' | items={}", lastName, result.size());

            // --- Détails (DEBUG)
            if (log.isDebugEnabled()) {
                log.debug("GET /personInfo - response payload: {}", result);
            }

            return result;
        } catch (Exception ex) {
            // --- Erreur/exception (ERROR)
            log.error("GET /personInfo - failure | lastName='{}' | error={}", lastName, ex.toString(), ex);
            throw ex; // re-propage : aucun changement de comportement
        }
    }
}

