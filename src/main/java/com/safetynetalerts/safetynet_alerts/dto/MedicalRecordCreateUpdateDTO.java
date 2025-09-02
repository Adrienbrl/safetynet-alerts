package com.safetynetalerts.safetynet_alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordCreateUpdateDTO {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/(19|20)\\d\\d$",
            message = "birthdate must be MM/dd/yyyy") private String birthdate;
    @NotNull private List<String> medications;
    @NotNull private List<String> allergies;
}
