package com.safetynetalerts.safetynet_alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

/**
 * Représente la couverture des données d'entrée pour créer ou mettre à jour un MedicalRecord.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordCreateUpdateDTO {
    @NotBlank private String firstName; // Prénom de la personne (obligatoire).
    @NotBlank private String lastName; // Nom de famille de la personne (obligatoire).
    @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/(19|20)\\d\\d$",
            message = "birthdate must be MM/dd/yyyy") private String birthdate; // Date de naissance au format US
    @NotNull private List<String> medications; // Liste des médicaments (peut être vide mais ne doit pas être null)
    @NotNull private List<String> allergies; // Liste des allergies (peut être vide mais ne doit pas être null)
}
