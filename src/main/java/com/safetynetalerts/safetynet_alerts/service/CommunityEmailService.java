package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service gérant l'endpoint "/communityEmail".
 *
 * Fournit la liste des adresses e-mail des habitants d'une ville.
 */
@Service
public class CommunityEmailService {

    private final DataRepository dataRepository;

    /**
     * Constructeur avec injection de dépendances.
     *
     * @param dataRepository dépôt de données utilisé pour récupérer les informations.
     */
    public CommunityEmailService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les e-mails des habitants d'une ville.
     *
     * @param city nom de la ville à rechercher.
     * @return liste triée d'adresses e-mail (sans valeurs nulles ni vides).
     */
    public List<String> getEmailsByCity(String city) {
        if (city == null || city.isBlank()) {
            return List.of();
        }

        String normalizedCity = city.trim();

        return dataRepository.getPersonsByCity(normalizedCity).stream()
                .map(Person::getEmail)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .sorted()
                .toList();
    }
}
