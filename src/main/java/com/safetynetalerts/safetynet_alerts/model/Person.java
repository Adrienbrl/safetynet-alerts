package com.safetynetalerts.safetynet_alerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une personne avec ses informations personnelles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String firstName; // Prénom
    private String lastName; // Nom
    private String address; // Son adresse
    private String city; // La ville
    private String zip; // Le code postal
    private String phone; // Numéro de téléphone
    private String email; // Adresse e-mail
}

