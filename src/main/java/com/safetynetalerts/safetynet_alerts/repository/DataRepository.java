package com.safetynetalerts.safetynet_alerts.repository;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.service.JsonDataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

import java.util.*;

/**
 * Repository est chargé de stocker et de fournir l'accès aux données chargées depuis le fichier JSON.
 */

@Repository
public class DataRepository {
    private final JsonDataLoader jsonDataLoader; // Charge les données depuis le fichier JSON

    private List<Person> persons; // Liste des personnes chargées
    private List<Firestation> firestations; // Liste des casernes chargées
    private List<MedicalRecord> medicalRecords; // Liste des dossiers médicaux chargés

    /**
     * Constructeur injectant le service de chargement JSON.
     *
     * @param jsonDataLoader instance de {@link JsonDataLoader} pour charger les données.
     */
    public DataRepository(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
    }

    /**
     * Méthode appelée après l'initialisation du bean Spring.
     *
     * Charge toutes les données depuis le JSON et les stocke dans des listes en mémoire.
     */
    @PostConstruct
    public void init() {
        this.persons = new ArrayList<>(jsonDataLoader.getData().getPersons());
        this.firestations = new ArrayList<>(jsonDataLoader.getData().getFirestations());
        this.medicalRecords = new ArrayList<>(jsonDataLoader.getData().getMedicalrecords());
    }

    /**
     * Récupère toutes les adresses couvertes par une caserne spécifique.
     *
     * @param stationNumber numéro de la caserne.
     * @return liste des adresses couvertes.
     */
    public List<String> getAddressesByStationNumber(int stationNumber) {
        return firestations.stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les personnes habitant à une liste d'adresses données.
     *
     * @param addresses liste des adresses.
     * @return liste des personnes correspondant aux adresses.
     */
    public List<Person> getPersonsByAddresses(List<String> addresses) {
        return persons.stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un dossier médical à partir du prénom et du nom.
     *
     * @param firstName prénom de la personne.
     * @param lastName nom de la personne.
     * @return un {@link Optional} contenant le dossier médical s'il est trouvé.
     */
    public Optional<MedicalRecord> getMedicalRecordByFirstAndLastName(String firstName, String lastName) {
        return medicalRecords.stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .findFirst();
    }

    /**
     * Récupère toutes les personnes vivant à une adresse spécifique.
     *
     * @param address adresse recherchée.
     * @return liste des personnes à cette adresse.
     */
    public List<Person> getPersonsByAddress(String address) {
        return persons.stream()
                .filter(p -> p.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public Optional<Integer> getStationNumberByAddress(String address) {
        return firestations.stream()
                .filter(fs -> fs.getAddress().equals(address))
                .map(Firestation::getStation)
                .findFirst();
    }

    public List<Person> getPersonsByLastName(String lastName) {
        return persons.stream()
                .filter(p -> p.getLastName() != null && p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public List<Person> getPersonsByCity(String city) {
        return persons.stream()
                .filter(p -> p.getCity() != null && p.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

}


