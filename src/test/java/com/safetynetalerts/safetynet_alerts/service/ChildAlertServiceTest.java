package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.ChildDTO;
import com.safetynetalerts.safetynet_alerts.dto.HouseholdMemberDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChildAlertServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks ChildAlertService service;

    // ---- HELPERS ----

    private static Person mkPerson(String first, String last) {
        // On mock Person pour éviter une dépendance à ses constructeurs
        Person p = mock(Person.class);
        when(p.getFirstName()).thenReturn(first);
        when(p.getLastName()).thenReturn(last);
        return p;
    }

    private static MedicalRecord mkMedicalRecordWithAgeYears(int years) {
        // birthdate = today - years
        LocalDate birth = LocalDate.now().minusYears(years);
        String birthStr = birth.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        MedicalRecord mr = mock(MedicalRecord.class);
        when(mr.getBirthdate()).thenReturn(birthStr);
        return mr;
    }

    // ---- TESTS ----

    @Test
    void should_return_child_with_other_household_members_when_address_has_child_and_adults() {
        String address = "1509 Culver St";
        Person child  = mkPerson("Emma",  "Miller");
        Person parent = mkPerson("Mack", "Miller");
        Person sister = mkPerson("Anna", "Miller");

        when(dataRepository.getPersonsByAddress(address))
                .thenReturn(List.of(child, parent, sister));

        // Fallback
        when(dataRepository.getMedicalRecordByFirstAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        MedicalRecord mrEmma  = mkMedicalRecordWithAgeYears(10);
        MedicalRecord mrMack = mkMedicalRecordWithAgeYears(40);
        MedicalRecord mrAnna = mkMedicalRecordWithAgeYears(16);

        when(dataRepository.getMedicalRecordByFirstAndLastName("Emma",  "Miller"))
                .thenReturn(Optional.of(mrEmma));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Mack", "Miller"))
                .thenReturn(Optional.of(mrMack));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Anna", "Miller"))
                .thenReturn(Optional.of(mrAnna));

        List<ChildDTO> result = service.getChildrenByAddress(address);

        assertEquals(2, result.size(), "Emma et Anna doivent être listées (<= 18 ans)");
        ChildDTO emma = result.stream().filter(c -> "Emma".equals(c.getFirstName()))
                .findFirst().orElseThrow();

        assertTrue(emma.getAge() <= 18);
        var othersEmma = emma.getOtherHouseholdMembers();
        assertEquals(2, othersEmma.size());
        assertTrue(othersEmma.stream().anyMatch(h -> "Mack".equals(h.getFirstName()) && "Miller".equals(h.getLastName())));
        assertTrue(othersEmma.stream().anyMatch(h -> "Anna".equals(h.getFirstName()) && "Miller".equals(h.getLastName())));
        assertFalse(othersEmma.stream().anyMatch(h -> "Emma".equals(h.getFirstName())));

        verify(dataRepository).getPersonsByAddress(address);
        verify(dataRepository, atLeast(3))
                .getMedicalRecordByFirstAndLastName(anyString(), anyString());
    }

    @Test
    void should_return_empty_list_when_address_has_only_adults() {
        String address = "42 Roaling St";
        Person a = mkPerson("Charlize", "Doe");
        Person b = mkPerson("John",   "Doe");

        when(dataRepository.getPersonsByAddress(address))
                .thenReturn(List.of(a, b));

        // Fallback
        when(dataRepository.getMedicalRecordByFirstAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // >>> Pré-crée les dossiers
        MedicalRecord mrCharlize = mkMedicalRecordWithAgeYears(33);
        MedicalRecord mrJohn   = mkMedicalRecordWithAgeYears(60);

        when(dataRepository.getMedicalRecordByFirstAndLastName("Charlize", "Doe"))
                .thenReturn(Optional.of(mrCharlize));
        when(dataRepository.getMedicalRecordByFirstAndLastName("John", "Doe"))
                .thenReturn(Optional.of(mrJohn));

        List<ChildDTO> result = service.getChildrenByAddress(address);

        assertTrue(result.isEmpty(), "Aucun enfant ne doit être listé");

        verify(dataRepository).getPersonsByAddress(address);
        verify(dataRepository, atLeast(2))
                .getMedicalRecordByFirstAndLastName(anyString(), anyString());
    }


    @Test
    void should_ignore_person_when_medical_record_is_missing() {
        // Given
        String address = "7 Unknown MR";
        Person child = mkPerson("Ariana", "Erza");
        when(dataRepository.getPersonsByAddress(address)).thenReturn(List.of(child));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Ariana", "Erza"))
                .thenReturn(Optional.empty()); // pas de dossier

        // When
        List<ChildDTO> result = service.getChildrenByAddress(address);

        // Then
        assertTrue(result.isEmpty(), "Sans dossier médical, la personne est ignorée");
        verify(dataRepository).getPersonsByAddress(address);
        verify(dataRepository).getMedicalRecordByFirstAndLastName("Ariana", "Erza");
        verifyNoMoreInteractions(dataRepository);
    }

}
