package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.ChildDTO;
import com.safetynetalerts.safetynet_alerts.dto.HouseholdMemberDTO;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import com.safetynetalerts.safetynet_alerts.utils.DateUtils;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service gérant l'endpoint '/childAlert'.
 *
 * Fournit la liste des enfants habitant à une adresse donnée ainsi que les autres membres de son foyer.
 */
@Service
public class ChildAlertService {

    private final DataRepository dataRepository;

    /**
     * Constructeur avec injection de dépendances.
     *
     * @param dataRepository dépôt de données utilisé pour récupérer les informations.
     */
    public ChildAlertService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère la liste des enfants et des autres membres du foyer pour une adresse donnée.
     *
     * @param address adresse à rechercher.
     * @return liste de {@link ChildDTO} représentant les enfants et leur foyer.
     */
    public List<ChildDTO> getChildrenByAddress(String address) {
        List<Person> persons = dataRepository.getPersonsByAddress(address); // Récupération des personnes habitant à l'adresse spécifiée
        List<ChildDTO> children = new ArrayList<>(); // Liste qui contiendra tous les enfants trouvés par l'adresse spécifiée

        // Parcours de chaque personne
        for (Person person : persons) {
            Optional<MedicalRecord> recordOpt = dataRepository // Recherche du dossier médical
                    .getMedicalRecordByFirstAndLastName(person.getFirstName(), person.getLastName());

            // Si le dossier médical de la personne existe alors on calcul son âge
            if (recordOpt.isPresent()) {
                int age = DateUtils.calculateAge(recordOpt.get().getBirthdate());

                // Si c'est un enfant de 18 ans ou moins
                if (age <= 18) {
                    List<HouseholdMemberDTO> otherMembers = persons.stream() // Récupération des autres membres du foyer
                            .filter(p -> !(p.getFirstName().equals(person.getFirstName()) &&
                                    p.getLastName().equals(person.getLastName())))
                            .map(p -> new HouseholdMemberDTO(p.getFirstName(), p.getLastName()))
                            .collect(Collectors.toList());

                    // Ajout à la liste des enfants
                    children.add(new ChildDTO(
                            person.getFirstName(),
                            person.getLastName(),
                            age,
                            otherMembers
                    ));
                }
            }
        }

        // Renvoie de la liste (qui peut être vide) des enfants trouvés
        return children;
    }
}


