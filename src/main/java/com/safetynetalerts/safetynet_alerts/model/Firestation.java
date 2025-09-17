package com.safetynetalerts.safetynet_alerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une caserne de pompiers avec ses infos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Firestation {
    private String address; // Adresse de la caserne
    private int station; // Numéro de la caserne
}

