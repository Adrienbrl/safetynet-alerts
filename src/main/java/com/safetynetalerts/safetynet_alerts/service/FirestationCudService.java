package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Service d'application pour les opérations CUD (Create/Update/Delete)
 */
@Service
@RequiredArgsConstructor
public class FirestationCudService {

    private final DataRepository dataRepository;

    /**
     * Crée une association firestation à partir d'un DTO.
     *
     * @param dto données validées (adresse, station)
     * @return l'entité persistée
     * @throws IllegalStateException si une association existe déjà pour l'adresse
     * @throws IllegalArgumentException si le repository considère l'entrée invalide
     */
    public Firestation create(FirestationCreateUpdateDTO dto) {
        Firestation f = toModel(dto);
        return dataRepository.addFirestation(f);
    }

    /**
     * Met à jour le numéro de station pour l'adresse fournie dans le DTO.
     *
     * @param dto données validées (porte l'adresse cible et la nouvelle station)
     * @return l'entité mise à jour
     * @throws NoSuchElementException si aucune association n'existe pour l'adresse
     */
    public Firestation update(FirestationCreateUpdateDTO dto) {
        return dataRepository.updateFirestation(dto.getAddress(), f -> f.setStation(dto.getStation()));
    }

    /**
     * Supprime l'association par adresse.
     *
     * @param address adresse cible
     * @throws NoSuchElementException si aucune association n'a été supprimée
     */
    public void deleteByAddress(String address) {
        boolean removed = dataRepository.deleteFirestationByAddress(address);
        if (!removed) throw new NoSuchElementException("Mapping not found");
    }

    /**
     * Supprime toutes les associations pour un numéro de station.
     *
     * @param station numéro de station
     * @throws NoSuchElementException si aucune association n'a été supprimée
     */
    public void deleteByStation(int station) {
        int removed = dataRepository.deleteFirestationsByStation(station);
        if (removed == 0) throw new NoSuchElementException("No mapping for station");
    }

    /**
     * Conversion utilitaire DTO → modèle.
     *
     * @param dto source validée
     * @return instance {@link Firestation} construite
     */
    private Firestation toModel(FirestationCreateUpdateDTO dto) {
        Firestation f = new Firestation();
        f.setAddress(dto.getAddress());
        f.setStation(dto.getStation());
        return f;
    }

}
