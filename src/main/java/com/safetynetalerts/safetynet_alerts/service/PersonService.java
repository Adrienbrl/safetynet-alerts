package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.PersonCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Service métier gérant la ressource {@link Person}.
 *
 * Fournit les opérations de création, mise à jour et suppression
 * en s'appuyant sur le dépôt de données.
 */
@Service
@RequiredArgsConstructor
public class PersonService {

    private final DataRepository dataRepository;

    /**
     * Crée une nouvelle personne à partir d'un DTO.
     *
     * @param dto données d'entrée pour la création.
     * @return la personne créée.
     * @throws IllegalStateException si une personne avec le même prénom/nom existe déjà.
     */
    public Person create(PersonCreateUpdateDTO dto) {
        Person p = toModel(dto);
        return dataRepository.addPerson(p);
    }

    /**
     * Met à jour une personne existante à partir d'un DTO.
     *
     * <p>La personne est identifiée par le couple (firstName, lastName) présent dans le DTO.</p>
     *
     * @param dto données de mise à jour.
     * @return la personne après mise à jour.
     * @throws NoSuchElementException si la personne n'existe pas.
     */
    public Person update(PersonCreateUpdateDTO dto) {
        return dataRepository.updatePerson(dto.getFirstName(), dto.getLastName(), p -> {
            p.setAddress(dto.getAddress());
            p.setCity(dto.getCity());
            p.setZip(dto.getZip());
            p.setPhone(dto.getPhone());
            p.setEmail(dto.getEmail());
        });
    }

    /**
     * Supprime une personne identifiée par prénom et nom.
     *
     * @param firstName prénom de la personne à supprimer.
     * @param lastName  nom de la personne à supprimer.
     * @throws NoSuchElementException si aucune personne ne correspond.
     */
    public void delete(String firstName, String lastName) {
        boolean removed = dataRepository.deletePerson(firstName, lastName);
        if (!removed) throw new NoSuchElementException("Person not found");
    }

    /**
     * Convertit un {@link PersonCreateUpdateDTO} en entité {@link Person}.
     *
     * @param dto source de données.
     * @return entité initialisée avec les champs du DTO.
     */
    private Person toModel(PersonCreateUpdateDTO dto) {
        Person p = new Person();
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setAddress(dto.getAddress());
        p.setCity(dto.getCity());
        p.setZip(dto.getZip());
        p.setPhone(dto.getPhone());
        p.setEmail(dto.getEmail());
        return p;
    }
}
