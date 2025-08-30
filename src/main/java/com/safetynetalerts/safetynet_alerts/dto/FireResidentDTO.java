package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

/**
 * Représente un résident associé à une adresse.
 *
 * Cette classe contient les informations de base d’une personne
 * nécessaires pour les réponses « fire » et « flood ».
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireResidentDTO {
    private String firstName; // Prénom
    private String lastName; // Nom
    private String phone; // Numéro de téléphone
    private Integer age; // Age
    private List<String> medications = new ArrayList<>(); // Liste des médicaments pris
    private List<String> allergies = new ArrayList<>(); // Liste des allergies connues
}

