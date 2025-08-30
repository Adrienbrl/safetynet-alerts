package com.safetynetalerts.safetynet_alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente la réponse pour l’endpoint « flood/stations ».
 *
 * Cette classe contient la liste des casernes concernées ainsi que
 * les foyers (households) couverts avec leurs résidents.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloodStationsResponseDTO {
    private List<Integer> stations; // Identifiants des casernes demandées
    private List<HouseholdDTO> households; // Foyers couverts par les casernes
}


