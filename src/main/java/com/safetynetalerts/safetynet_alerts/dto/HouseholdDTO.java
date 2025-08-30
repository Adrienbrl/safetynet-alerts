package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente un foyer à une adresse donnée.
 *
 * Cette classe contient l’adresse du foyer ainsi que la liste
 * des résidents présents à cette adresse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDTO {
    private String address; // Adresse du foyer
    private List<FireResidentDTO> residents; // Résidents vivant à cette adresse
}

