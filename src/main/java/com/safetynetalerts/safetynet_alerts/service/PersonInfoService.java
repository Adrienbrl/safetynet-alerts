package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.PersonInfoDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import com.safetynetalerts.safetynet_alerts.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonInfoService {

    private final DataRepository dataRepository;

    public PersonInfoService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public List<PersonInfoDTO> getPersonsInfoByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return List.of();
        }
        String ln = lastName.trim();

        List<Person> persons = dataRepository.getPersonsByLastName(ln);

        return persons.stream()
                .map(p -> {
                    Optional<MedicalRecord> mrOpt =
                            dataRepository.getMedicalRecordByFirstAndLastName(p.getFirstName(), p.getLastName());

                    int age = mrOpt.map(mr -> DateUtils.calculateAge(mr.getBirthdate())).orElse(0);
                    List<String> medications = mrOpt.map(MedicalRecord::getMedications).orElse(List.of());
                    List<String> allergies = mrOpt.map(MedicalRecord::getAllergies).orElse(List.of());

                    return new PersonInfoDTO(
                            p.getFirstName(),
                            p.getLastName(),
                            p.getAddress(),
                            age,
                            p.getEmail(),
                            medications,
                            allergies
                    );
                })
                .toList();
    }
}
