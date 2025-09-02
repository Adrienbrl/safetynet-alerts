package com.safetynetalerts.safetynet_alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestationCreateUpdateDTO {
    @NotBlank private String address;
    @NotBlank @Positive int station;
}

