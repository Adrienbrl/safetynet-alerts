package com.safetynetalerts.safetynet_alerts.dto;

import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Conteneur regroupant toutes les données chargées depuis le fichier JSON.
 *
 * DataContainer regroupe la liste des personnes, casernes et dossiers médicaux.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataContainer {
    private List<Person> persons; // Liste de toutes les personnes
    private List<Firestation> firestations; // Liste des casernes
    private List<MedicalRecord> medicalrecords; // Liste des dossiers médicaux
}


