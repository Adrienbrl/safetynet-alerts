package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Représente la couverture d'une caserne de pompiers.
 *
 * Cette classe contient la liste des personnes couvertes par une caserne ainsi
 * que le nombre d'adultes et d'enfants.
 */
@Data
@AllArgsConstructor
public class FirestationCoverageDTO {
    private List<CoveredPersonDTO> coveredPersons; // Liste des personnes couvertes par la caserne
    private int numberOfAdults; // Nombre total d'adultes couverts
    private int numberOfChildren; // Nombre total d'enfants couverts
}

