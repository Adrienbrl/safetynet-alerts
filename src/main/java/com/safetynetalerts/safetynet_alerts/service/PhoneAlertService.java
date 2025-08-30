package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service récupérant les numéros de téléphone des personnes couvertes par une caserne spécifique.
 */
@Service
public class PhoneAlertService {

    private final DataRepository dataRepository;

    public PhoneAlertService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère la liste des numéros de téléphone uniques des habitants
     * couverts par une caserne donnée.
     *
     * @param stationNumber numéro de la caserne.
     * @return liste triée et unique des numéros de téléphone.
     */
    public List<String> getPhonesByStation(int stationNumber) {
        // Si le numéro de la caserne est de 0 ou moins, on renvoie une liste vide
        if (stationNumber <= 0) {
            return List.of();
        }

        // Récupère toutes les adresses couvertes par la caserne ayant ce numéro
        List<String> addresses = dataRepository.getAddressesByStationNumber(stationNumber);
        // Si aucune adresse n'est couverte par cette caserne, on renvoie une liste vide
        if (addresses.isEmpty()) {
            return List.of();
        }

        // Renvoie toutes les personnes habitant aux adresses couvertes par la caserne
        return dataRepository.getPersonsByAddresses(addresses).stream()
                .map(Person::getPhone) // Extraction du numéro
                .filter(Objects::nonNull) // Retire les valeurs null
                .map(String::trim) // Nettoie les espaces
                .filter(s -> !s.isEmpty()) // Retire les chaînes vides
                .distinct() // Supprime les doublons
                .sorted() // Trie la liste
                .toList();
    }
}

