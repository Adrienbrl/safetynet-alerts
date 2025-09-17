package com.safetynetalerts.safetynet_alerts.repository;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.service.JsonDataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;
import java.util.function.Consumer;

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

    /**
     * Recherche le numéro de caserne couvrant une adresse.
     *
     * @param address adresse exacte à rechercher.
     * @return un {@link Optional} contenant le numéro de la caserne s’il est trouvé.
     */
    public Optional<Integer> getStationNumberByAddress(String address) {
        return firestations.stream()
                .filter(fs -> fs.getAddress().equals(address))
                .map(Firestation::getStation)
                .findFirst();
    }

    /**
     * Récupère toutes les personnes portant un nom de famille donné.
     *
     * @param lastName nom de famille recherché (insensible à la casse).
     * @return liste des personnes correspondantes.
     */
    public List<Person> getPersonsByLastName(String lastName) {
        return persons.stream()
                .filter(p -> p.getLastName() != null && p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les personnes d’une ville.
     *
     * @param city nom de la ville (insensible à la casse).
     * @return liste des personnes habitant la ville.
     */
    public List<Person> getPersonsByCity(String city) {
        return persons.stream()
                .filter(p -> p.getCity() != null && p.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    /**
     * Construit une clé normalisée « first|last » pour identifier une personne.
     *
     * @param firstName prénom (peut être {@code null}).
     * @param lastName nom (peut être {@code null}).
     * @return clé concaténée au format {@code prenom|nom}.
     */
    private String key(String firstName, String lastName) {
        return (firstName == null ? "" : firstName.trim().toLowerCase()) + "|" +
                (lastName == null ? "" : lastName.trim().toLowerCase());
    }

    /**
     * Recherche une personne par prénom et nom à l’aide de la clé normalisée.
     *
     * @param firstName prénom de la personne.
     * @param lastName nom de la personne.
     * @return un {@link Optional} contenant la personne si elle est trouvée.
     */
    public Optional<Person> findPersonByFirstAndLastName(String firstName, String lastName) {
        String k = key(firstName, lastName);
        return persons.stream()
                .filter(p -> key(p.getFirstName(), p.getLastName()).equals(k))
                .findFirst();
    }

    /**
     * Indique si une personne existe déjà dans le référentiel.
     *
     * @param firstName prénom de la personne.
     * @param lastName nom de la personne.
     * @return {@code true} si une correspondance est trouvée, sinon {@code false}.
     */
    public boolean personExists(String firstName, String lastName) {
        return findPersonByFirstAndLastName(firstName, lastName).isPresent();
    }

    /**
     * Ajoute une nouvelle personne au référentiel.
     *
     * @param p personne à ajouter.
     * @return la personne ajoutée.
     * @throws IllegalArgumentException si {@code p} est {@code null}.
     * @throws IllegalStateException si une personne avec le même prénom et nom existe déjà.
     */
    public Person addPerson(Person p) {
        if (p == null) throw new IllegalArgumentException("Person cannot be null");
        if (personExists(p.getFirstName(), p.getLastName())) {
            throw new IllegalStateException("Person already exists");
        }
        persons.add(p);
        return p;
    }

    /**
     * Met à jour une personne existante identifiée par prénom et nom.
     *
     * <p>Méthode synchronisée.</p>
     *
     * @param firstName prénom de la personne à mettre à jour.
     * @param lastName nom de la personne à mettre à jour.
     * @param updater action appliquant les modifications sur l’entité trouvée.
     * @return la personne après mise à jour.
     * @throws java.util.NoSuchElementException si la personne n’existe pas.
     */
    public synchronized Person updatePerson(String firstName, String lastName, Consumer<Person> updater) {
        Person current = findPersonByFirstAndLastName(firstName, lastName)
                .orElseThrow(() -> new NoSuchElementException("Person not found"));
        updater.accept(current);
        return current;
    }

    /**
     * Supprime une personne identifiée par prénom et nom.
     *
     * <p>Méthode synchronisée.</p>
     *
     * @param firstName prénom de la personne à supprimer.
     * @param lastName nom de la personne à supprimer.
     * @return {@code true} si au moins une entrée a été supprimée, sinon {@code false}.
     */
    public synchronized boolean deletePerson(String firstName, String lastName) {
        return persons.removeIf(p ->
                key(p.getFirstName(), p.getLastName()).equals(key(firstName, lastName))
        );
    }

    /**
     * Normalise une adresse pour comparaison insensible à la casse et aux espaces.
     *
     * <p>Renvoie une chaîne en minuscules, sans espaces en début/fin. Renvoie
     * une chaîne vide si {@code address} est {@code null} pour éviter les NPE.</p>
     *
     * @param address adresse en entrée (peut être {@code null})
     * @return adresse normalisée, jamais {@code null}
     */
    private String addrKey(String address) {
        return address == null ? "" : address.trim().toLowerCase();
    }

    /**
     * Recherche l'association {@code Firestation} correspondant à une adresse.
     *
     * <p>La comparaison est effectuée sur l'adresse normalisée (voir {@link #addrKey(String)})
     * pour être robuste aux variations de casse et d'espaces.</p>
     *
     * @param address adresse recherchée
     * @return un {@code Optional} contenant la première correspondance, ou vide si aucune
     */
    public Optional<Firestation> findFirestationByAddress(String address) {
        String k = addrKey(address);
        return firestations.stream()
                .filter(f -> addrKey(f.getAddress()).equals(k))
                .findFirst();
    }

    /**
     * Indique si une association existe déjà pour l'adresse donnée.
     *
     * @param address adresse à tester
     * @return {@code true} si une association existe, {@code false} sinon
     */
    public boolean firestationExists(String address) {
        return findFirestationByAddress(address).isPresent();
    }

    /**
     * Ajoute une nouvelle association {@code Firestation}.
     *
     * <p>Vérifie que l'objet n'est pas {@code null} et qu'il n'existe pas déjà
     * une association pour l'adresse indiquée. La vérification de doublon se fait
     * via {@link #firestationExists(String)}.</p>
     *
     * @param f association à ajouter
     * @return la même instance que celle ajoutée
     * @throws IllegalArgumentException si {@code f} est {@code null}
     * @throws IllegalStateException si une association existe déjà pour l'adresse
     */
    public Firestation addFirestation(Firestation f) {
        if (f == null) throw new IllegalArgumentException("Firestation cannot be null");
        if (firestationExists(f.getAddress())) {
            throw new IllegalStateException("Mapping for address already exists");
        }
        firestations.add(f);
        return f;
    }

    /**
     * Met à jour <em>en place</em> l'association {@code Firestation} ciblée par son adresse.
     *
     * <p><b>Concurrence :</b> la méthode est {@code synchronized} pour sérialiser l'accès
     * en écriture sur la collection sous-jacente.</p>
     *
     * <p><b>Contrat de l'updater :</b> le {@link java.util.function.Consumer} reçu doit
     * modifier l'instance existante (muter ses champs) et ne pas tenter de la remplacer
     * par une nouvelle instance.</p>
     *
     * @param address adresse de l'association à mettre à jour
     * @param updater fonction appliquant les modifications sur l'objet trouvé
     * @return l'objet mis à jour (même référence)
     * @throws NoSuchElementException si aucune association n'est trouvée pour l'adresse
     */
    public synchronized Firestation updateFirestation(String address, java.util.function.Consumer<Firestation> updater) {
        Firestation current = findFirestationByAddress(address)
                .orElseThrow(() -> new NoSuchElementException("Mapping not found"));
        updater.accept(current);
        return current;
    }

    /**
     * Supprime l'association {@code Firestation} correspondant à l'adresse fournie.
     *
     * <p>La comparaison d'adresse est normalisée via {@link #addrKey(String)}.</p>
     *
     * @param address adresse cible
     * @return {@code true} si au moins une association a été supprimée, {@code false} sinon
     */
    public synchronized boolean deleteFirestationByAddress(String address) {
        String k = addrKey(address);
        return firestations.removeIf(f -> addrKey(f.getAddress()).equals(k));
    }

    /**
     * Supprime toutes les associations liées à un numéro de station.
     *
     * @param station numéro de station
     * @return le nombre d’associations supprimées (>= 0)
     */
    public synchronized int deleteFirestationsByStation(int station) {
        int before = firestations.size();
        firestations.removeIf(f -> f.getStation() == station);
        return before - firestations.size();
    }

    /**
     * Recherche un dossier médical par prénom + nom.
     *
     * <p>Utilise la clé normalisée produite par {@code key(firstName, lastName)}
     * pour une comparaison robuste à la casse/espaces selon ton implémentation.</p>
     *
     * @param firstName prénom
     * @param lastName  nom
     * @return un {@code Optional} contenant la première correspondance, ou vide
     */
    public Optional<MedicalRecord> findMedicalRecordByFirstAndLastName(String firstName, String lastName) {
        String k = key(firstName, lastName);
        return medicalRecords.stream()
                .filter(mr -> key(mr.getFirstName(), mr.getLastName()).equals(k))
                .findFirst();
    }

    /**
     * Indique si un dossier médical existe pour la personne donnée.
     *
     * @param firstName prénom
     * @param lastName  nom
     * @return {@code true} si un dossier existe, {@code false} sinon
     */
    public boolean medicalRecordExists(String firstName, String lastName) {
        return findMedicalRecordByFirstAndLastName(firstName, lastName).isPresent();
    }

    /**
     * Ajoute un nouveau dossier médical.
     *
     * @param mr dossier à ajouter
     * @return l’instance ajoutée (même référence)
     * @throws IllegalArgumentException si {@code mr} est {@code null}
     * @throws IllegalStateException si un dossier existe déjà pour {firstName,lastName}
     */
    public MedicalRecord addMedicalRecord(MedicalRecord mr) {
        if (mr == null) throw new IllegalArgumentException("MedicalRecord cannot be null");
        if (medicalRecordExists(mr.getFirstName(), mr.getLastName())) {
            throw new IllegalStateException("Medical record already exists");
        }
        medicalRecords.add(mr);
        return mr;
    }

    /**
     * Met à jour <em>en place</em> le dossier médical identifié par {firstName,lastName}.
     *
     * <p><b>Concurrence :</b> méthode {@code synchronized} (écritures sérialisées).</p>
     * <p><b>Contrat de l'updater :</b> le {@link java.util.function.Consumer} doit muter
     * l’instance existante (ne pas la remplacer).</p>
     *
     * @param firstName prénom
     * @param lastName  nom
     * @param updater   fonction appliquant les modifications sur l'objet trouvé
     * @return l'objet mis à jour (même référence)
     * @throws NoSuchElementException si aucun dossier n'est trouvé
     */
    public synchronized MedicalRecord updateMedicalRecord(
            String firstName, String lastName, java.util.function.Consumer<MedicalRecord> updater) {
        MedicalRecord current = findMedicalRecordByFirstAndLastName(firstName, lastName)
                .orElseThrow(() -> new NoSuchElementException("Medical record not found"));
        updater.accept(current);
        return current;
    }

    /**
     * Supprime le dossier médical de la personne {firstName,lastName}.
     *
     * @param firstName prénom
     * @param lastName  nom
     * @return {@code true} si au moins un dossier a été supprimé, {@code false} sinon
     */
    public synchronized boolean deleteMedicalRecord(String firstName, String lastName) {
        String k = key(firstName, lastName);
        return medicalRecords.removeIf(mr -> key(mr.getFirstName(), mr.getLastName()).equals(k));
    }

}


