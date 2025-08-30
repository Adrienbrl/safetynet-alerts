package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Représente les données d’entrée pour créer ou mettre à jour une personne.
 *
 * Cette classe contient les champs nécessaires à l’enregistrement d’une personne
 * et applique des contraintes de validation pour garantir l’intégrité des données.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCreateUpdateDTO {
    @NotBlank private String firstName; // Prénom
    @NotBlank private String lastName; // Nom
    @NotBlank private String address; // Adresse
    @NotBlank private String city; // Ville
    @NotBlank private String zip; // Code postal
    @NotBlank private String phone; // Numéro de téléphone
    @NotBlank @Email private String email; // Adresse e-mail
}
