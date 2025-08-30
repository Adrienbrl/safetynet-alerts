package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FireAddressResponseDTO;
import com.safetynetalerts.safetynet_alerts.dto.FireResidentDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import com.safetynetalerts.safetynet_alerts.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service gérant l'endpoint "/fire".
 *
 * Fournit les informations d'une adresse : caserne couvrante et liste des résidents
 * (âge, téléphone, médicaments, allergies).
 */
@Service
public class FireService {

    private final DataRepository dataRepository;

    /**
     * Constructeur avec injection de dépendances.
     *
     * @param dataRepository dépôt de données utilisé pour récupérer les informations.
     */
    public FireService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les informations « fire » pour une adresse donnée.
     *
     * @param address adresse à rechercher.
     * @return un {@link Optional} contenant la réponse si la couverture est trouvée.
     */
    public Optional<FireAddressResponseDTO> getFireInfoByAddress(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }
        String addr = address.trim();

        Optional<Integer> stationOpt = dataRepository.getStationNumberByAddress(addr);
        if (stationOpt.isEmpty()) {
            return Optional.empty();
        }

        List<Person> persons = dataRepository.getPersonsByAddress(addr);
        List<FireResidentDTO> residents = persons.stream()
                .map(p -> { Optional<MedicalRecord> mrOpt =
                        dataRepository.getMedicalRecordByFirstAndLastName(p.getFirstName(), p.getLastName());

                    Integer age = null;
                    List<String> meds = new ArrayList<>();
                    List<String> allergies = new ArrayList<>();

                    if (mrOpt.isPresent()) {
                        MedicalRecord mr = mrOpt.get();
                        age = DateUtils.calculateAge(mr.getBirthdate());
                        if (mr.getMedications() != null) meds = mr.getMedications();
                        if (mr.getAllergies() != null) allergies = mr.getAllergies();
                    }

                    return new FireResidentDTO(
                            p.getFirstName(),
                            p.getLastName(),
                            p.getPhone(),
                            age,
                            meds,
                            allergies
                    );
                })
                .collect(Collectors.toList());

        FireAddressResponseDTO response = new FireAddressResponseDTO(
                List.of(stationOpt.get()),
                residents
        );

        return Optional.of(response);
    }
}
