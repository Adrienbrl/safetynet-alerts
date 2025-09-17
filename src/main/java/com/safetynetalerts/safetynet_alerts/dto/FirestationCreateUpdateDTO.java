package com.safetynetalerts.safetynet_alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Représente la couverture des données d'entrée pour créer ou mettre à jour une association
 * adresse ↔ numéro de station de pompiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestationCreateUpdateDTO {
    @NotBlank private String address; // Adresse civique couverte par la station (obligatoire, non vide, non blanc).
    @NotNull @Positive int station; // Numéro de station de pompiers (doit être strictement positif).
}

