package com.safetynetalerts.safetynet_alerts.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente les informations liées à une adresse pour l’endpoint « fire ».
 *
 * Cette classe contient la liste des numéros de casernes couvrant l’adresse
 * ainsi que la liste des résidents avec leurs informations utiles aux secours.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "stationNumbers", "residents" })
public class FireAddressResponseDTO {
    private List<Integer> stationNumbers = new ArrayList<>(); // Numéros des casernes couvrant l’adresse
    private List<FireResidentDTO> residents = new ArrayList<>(); // Habitants à l’adresse avec détails (âge, téléphone, etc.)
}


