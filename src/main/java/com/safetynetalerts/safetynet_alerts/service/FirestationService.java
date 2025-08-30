package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.CoveredPersonDTO;
import com.safetynetalerts.safetynet_alerts.dto.FirestationCoverageDTO;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.springframework.stereotype.Service;
import com.safetynetalerts.safetynet_alerts.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service gérant les informations de couverture des casernes.
 *
 * Permet de récupérer les personnes couvertes par une caserne spécifique
 * ainsi que le nombre d'adultes et d'enfants.
 */
@Service
public class FirestationService {

    private final DataRepository dataRepository;

    public FirestationService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les personnes couvertes par une caserne ainsi que le nombre d'adultes et d'enfants.
     *
     * @param stationNumber numéro de la caserne.
     * @return objet {@link FirestationCoverageDTO} contenant la liste des personnes et les statistiques.
     */
    public FirestationCoverageDTO getPersonsCoveredByStation(int stationNumber) {
        // Récupération de toutes les adresses couvertes par la caserne spécifiée
        List<String> addresses = dataRepository.getAddressesByStationNumber(stationNumber);
        // Récupération de toutes les personnes habitant à ces adresses.
        List<Person> persons = dataRepository.getPersonsByAddresses(addresses);
        // Liste qui contiendra les personnes couvertes par une caserne spécifique
        List<CoveredPersonDTO> coveredDTOs = new ArrayList<>();

        // Compteur pour le nombre d'adultes et d'enfants
        int adults = 0;
        int children = 0;

        // Parcours de chaque personne
        for (Person person : persons) {
            Optional<MedicalRecord> recordOpt = dataRepository.getMedicalRecordByFirstAndLastName(
                    person.getFirstName(), person.getLastName() // Recherche du dossier médical
            );

            // Si dossier existant, calcul de l'âge
            if (recordOpt.isPresent()) {
                String birthdate = recordOpt.get().getBirthdate();
                int age = DateUtils.calculateAge(birthdate);

                if (age <= 18) {
                    children++; // Si <= 18 compte comme enfant
                } else {
                    adults++; // Sinon compte comme adulte
                }

                // Ajout à la liste des personnes
                coveredDTOs.add(new CoveredPersonDTO(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getPhone()
                ));
            }
        }

        // Renvoie la liste des personnes couvertes par une caserne spécifiée ainsi que le nombre total d'enfants et adultes
        return new FirestationCoverageDTO(coveredDTOs, adults, children);
    }
}


