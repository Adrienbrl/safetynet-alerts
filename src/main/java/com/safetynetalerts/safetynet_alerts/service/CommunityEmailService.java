package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CommunityEmailService {

    private final DataRepository dataRepository;

    public CommunityEmailService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

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
