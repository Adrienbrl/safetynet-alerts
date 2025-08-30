package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FireResidentDTO;
import com.safetynetalerts.safetynet_alerts.dto.FloodStationsResponseDTO;
import com.safetynetalerts.safetynet_alerts.dto.HouseholdDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import com.safetynetalerts.safetynet_alerts.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service gérant l'endpoint "/flood/stations".
 *
 * Fournit les foyers (households) couverts par un ensemble de casernes.
 */
@Service
public class FloodService {

    private final DataRepository dataRepository;

    /**
     * Constructeur avec injection de dépendances.
     *
     * @param dataRepository dépôt de données utilisé pour récupérer les informations.
     */
    public FloodService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les foyers couverts par les casernes spécifiées.
     *
     * @param stations identifiants des casernes.
     * @return un {@link FloodStationsResponseDTO} contenant les foyers par adresse.
     */
    public FloodStationsResponseDTO getHouseholdsByStations(List<Integer> stations) {
        Set<String> addresses = stations.stream()
                .flatMap(station -> dataRepository.getAddressesByStationNumber(station).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<HouseholdDTO> households = addresses.stream()
                .map(address -> {
                    List<Person> personsAtAddress = dataRepository.getPersonsByAddress(address);

                    List<FireResidentDTO> residents = personsAtAddress.stream()
                            .map(p -> {
                                Optional<MedicalRecord> mrOpt = dataRepository
                                        .getMedicalRecordByFirstAndLastName(p.getFirstName(), p.getLastName());

                                Integer age = mrOpt.map(MedicalRecord::getBirthdate)
                                        .map(DateUtils::calculateAge)
                                        .orElse(null);

                                List<String> medications = mrOpt.map(MedicalRecord::getMedications)
                                        .orElseGet(Collections::emptyList);
                                List<String> allergies = mrOpt.map(MedicalRecord::getAllergies)
                                        .orElseGet(Collections::emptyList);

                                return new FireResidentDTO(
                                        p.getFirstName(),
                                        p.getLastName(),
                                        p.getPhone(),
                                        age,
                                        new ArrayList<>(medications),
                                        new ArrayList<>(allergies)
                                );
                            })
                            .collect(Collectors.toList());

                    return new HouseholdDTO(address, residents);
                })
                .collect(Collectors.toList());

        return new FloodStationsResponseDTO(stations, households);
    }
}
