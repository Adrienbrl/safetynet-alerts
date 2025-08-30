package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente un membre d'un foyer.
 *
 * Utilisé pour l'endpoint 'childAlert/' afin de fournir la liste des membres du foyer d'un enfant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberDTO {
    private String firstName; // Prénom
    private String lastName; // Nom
}



