package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Représente une personne couverte par une caserne de pompiers.
 */
@Data
@AllArgsConstructor
public class CoveredPersonDTO {
    private String firstName; // Prénom
    private String lastName; // Nom
    private String address; // Adresse
    private String phone; // Numéro de téléphone
}

