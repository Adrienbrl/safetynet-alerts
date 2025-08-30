package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.service.CommunityEmailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/communityEmail".
 * Il permet de récupérer la liste des adresses e-mail des habitants d'une ville donnée.
 */
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
    public List<String> getCommunityEmails(@RequestParam String city) {
        return communityEmailService.getEmailsByCity(city);
    }
}
