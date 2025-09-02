package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.MedicalRecordCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final DataRepository dataRepository;

    public MedicalRecord create(MedicalRecordCreateUpdateDTO dto) {
        MedicalRecord mr = toModel(dto);
        return dataRepository.addMedicalRecord(mr);
    }

    public MedicalRecord update(MedicalRecordCreateUpdateDTO dto) {
        return dataRepository.updateMedicalRecord(dto.getFirstName(), dto.getLastName(), mr -> {
            mr.setBirthdate(dto.getBirthdate());
            mr.setMedications(dto.getMedications());
            mr.setAllergies(dto.getAllergies());
        });
    }

    public void delete(String firstName, String lastName) {
        boolean removed = dataRepository.deleteMedicalRecord(firstName, lastName);
        if (!removed) throw new NoSuchElementException("Medical record not found");
    }

    private MedicalRecord toModel(MedicalRecordCreateUpdateDTO dto) {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName(dto.getFirstName());
        mr.setLastName(dto.getLastName());
        mr.setBirthdate(dto.getBirthdate());
        mr.setMedications(dto.getMedications());
        mr.setAllergies(dto.getAllergies());
        return mr;
    }
}
