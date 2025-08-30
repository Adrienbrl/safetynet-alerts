package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.service.PhoneAlertService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les requêtes pour l'endpoint "/phoneAlert".
 * Il permet de récupérer les numéros de téléphone des habitants couverts par une caserne donnée.
 */
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
        // Vérifie si le numéro de station est valide
        if (station <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // Récupère les numéros de téléphone liés à cette caserne
        List<String> phones = phoneAlertService.getPhonesByStation(station);
        // Retourne la liste des téléphones en réponse JSON
        return ResponseEntity.ok(phones);
    }
}

