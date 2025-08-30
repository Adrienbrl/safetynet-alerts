package com.safetynetalerts.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynetalerts.safetynet_alerts.dto.DataContainer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service permettant de charger les données depuis un fichier JSON au démarrage de l'application.
 */
@Service
public class JsonDataLoader {

    // Utilitaire Jackson pour désérialiser le JSON
    private final ObjectMapper objectMapper = new ObjectMapper();
    private DataContainer dataContainer; // Conteneur contenant toutes les données chargées

    // Charge les données depuis le fichier `data.json` après le démarrage de l'application
    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
            if (is == null) {
                throw new RuntimeException("Le fichier data.json est introuvable dans le dossier resources !");
            }
            dataContainer = objectMapper.readValue(is, DataContainer.class);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du parsing du fichier JSON", e);
        }
    }

    /**
     * Retourne les données chargées.
     *
     * @return un {@link DataContainer} contenant toutes les données.
     */
    public DataContainer getData() {
        return dataContainer;
    }
}



