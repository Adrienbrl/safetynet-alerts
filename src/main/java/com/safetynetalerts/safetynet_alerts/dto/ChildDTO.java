package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente les informations d'un enfant ainsi que les autres membres de son foyer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildDTO {
    private String firstName; // Prénom
    private String lastName; // Nom
    private int age; // L'âge
    private List<HouseholdMemberDTO> otherHouseholdMembers; // Liste des autres membres du foyer
}




