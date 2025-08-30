package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente les informations détaillées d’une personne.
 *
 * Cette classe contient les données retournées par l’endpoint « personInfo »,
 * notamment l’identité, les coordonnées, l’âge et les informations médicales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfoDTO {
    private String firstName; // Prénom
    private String lastName; // Nom
    private String address; // Adresse
    private int age; // Age
    private String email; // Adresse e-mail
    private List<String> medications; // Médicaments pris par la personne
    private List<String> allergies; // Allergies connues
}
