package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.service.CommunityEmailService;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/communityEmail".
 * Il permet de récupérer la liste des adresses e-mail des habitants d'une ville donnée.
 */
@Slf4j
@RestController
@RequestMapping(value = "/communityEmail", produces = "application/json")
public class CommunityEmailController {

    // Service métier responsable de la logique de récupération des e-mails communautaires.
    private final CommunityEmailService communityEmailService;

    /**
     * Constructeur avec injection du service CommunityEmailService.
     *
     * @param communityEmailService service gérant la récupération des adresses e-mail par ville
     */
    public CommunityEmailController(CommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
    }

    /**
     * Récupère la liste des adresses e-mail pour une ville.
     *
     * @param city nom de la ville à rechercher
     * @return liste des adresses e-mail
     */
    @GetMapping
    public List<String> getEmailsByCity(@RequestParam String city) {
        log.info("GET /communityEmail - request received | city='{}'", city);
        try {
            List<String> result = communityEmailService.getEmailsByCity(city);
            log.info("GET /communityEmail - success | city='{}' | items={}", city, result.size());
            if (log.isDebugEnabled()) {
                log.debug("GET /communityEmail - response payload: {}", result);
            }
            return result;
        } catch (Exception ex) {
            log.error("GET /communityEmail - failure | city='{}' | error={}", city, ex.toString(), ex);
            throw ex;
        }
    }
}
