package com.safetynetalerts.safetynet_alerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente un dossier médical d'une personne.
 */
@Data // Génère automatiquement getters, setters, equals, hashCode et toString
@NoArgsConstructor // Génère un constructeur sans argument.
@AllArgsConstructor // Génère un constructeur avec tous les arguments.
public class MedicalRecord {
    private String firstName; // Prénom
    private String lastName; // Nom
    private String birthdate; // Date de naissance
    private List<String> medications; // Liste des médicaments pris par une personne
    private List<String> allergies; // Liste des allergies connues d'une personne
}

