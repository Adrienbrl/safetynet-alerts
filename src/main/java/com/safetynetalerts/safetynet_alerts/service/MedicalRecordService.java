package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.MedicalRecordCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Service d'application pour les opérations CUD sur les MedicalRecord.
 */
@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final DataRepository dataRepository;

    /**
     * Crée un dossier médical à partir d'un DTO.
     *
     * @param dto données validées
     * @return l'entité persistée
     * @throws IllegalStateException si un dossier existe déjà pour {firstName,lastName}
     * @throws IllegalArgumentException si entrée invalide côté repository
     */
    public MedicalRecord create(MedicalRecordCreateUpdateDTO dto) {
        MedicalRecord mr = toModel(dto);
        return dataRepository.addMedicalRecord(mr);
    }

    /**
     * Met à jour un dossier médical existant (naissance, médicaments, allergies).
     *
     * <p>Identifie l'entité par {firstName,lastName} et applique les nouvelles
     * valeurs en place.</p>
     *
     * @param dto données validées portant l'identité et les champs à mettre à jour
     * @return l'entité mise à jour
     * @throws NoSuchElementException si aucun dossier n'est trouvé
     */
    public MedicalRecord update(MedicalRecordCreateUpdateDTO dto) {
        return dataRepository.updateMedicalRecord(dto.getFirstName(), dto.getLastName(), mr -> {
            mr.setBirthdate(dto.getBirthdate());
            mr.setMedications(dto.getMedications());
            mr.setAllergies(dto.getAllergies());
        });
    }

    /**
     * Supprime le dossier médical d'une personne.
     *
     * @param firstName prénom de la personne
     * @param lastName  nom de la personne
     * @throws NoSuchElementException si aucun dossier correspondant n'a été supprimé
     */
    public void delete(String firstName, String lastName) {
        boolean removed = dataRepository.deleteMedicalRecord(firstName, lastName);
        if (!removed) throw new NoSuchElementException("Medical record not found");
    }

    /**
     * Conversion utilitaire DTO → modèle.
     *
     * @param dto source validée
     * @return instance {@link MedicalRecord} construite
     */
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
