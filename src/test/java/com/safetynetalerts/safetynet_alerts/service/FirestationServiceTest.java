package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCoverageDTO;
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
class FirestationServiceTest {

    @Mock
    DataRepository dataRepository;

    @InjectMocks
    FirestationService service;

    // ---- HELPERS ----

    private static final DateTimeFormatter BIRTH_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private Person mkPerson(String first, String last, String address, String phone) {
        Person p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(address);
        p.setPhone(phone);
        return p;
    }

    private MedicalRecord mkMedicalRecordWithAgeYears(int years) {
        String birth = LocalDate.now().minusYears(years).format(BIRTH_FMT);
        MedicalRecord mr = new MedicalRecord();
        mr.setBirthdate(birth);
        mr.setMedications(List.of());
        mr.setAllergies(List.of());
        return mr;
    }

    // ---- TESTS ----

    @Test
    void should_return_people_list_and_correct_counts_for_mixed_ages() {
        // Given
        int station = 3;
        List<String> addresses = List.of("1509 Culver St", "42 Roaling St");

        Person mack = mkPerson("Mack", "Miller", "1509 Culver St", "111-222");
        Person emma = mkPerson("Emma", "Miller", "42 Roaling St", "333-444");
        Person anna = mkPerson("Anna", "Miller", "42 Roaling St", "555-666");

        when(dataRepository.getAddressesByStationNumber(station))
                .thenReturn(addresses);
        when(dataRepository.getPersonsByAddresses(addresses))
                .thenReturn(List.of(mack, emma, anna));

        when(dataRepository.getMedicalRecordByFirstAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        MedicalRecord mrMack = mkMedicalRecordWithAgeYears(40);
        MedicalRecord mrEmma = mkMedicalRecordWithAgeYears(10);
        MedicalRecord mrAnna = mkMedicalRecordWithAgeYears(16);

        when(dataRepository.getMedicalRecordByFirstAndLastName("Mack", "Miller"))
                .thenReturn(Optional.of(mrMack));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Emma", "Miller"))
                .thenReturn(Optional.of(mrEmma));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Anna", "Miller"))
                .thenReturn(Optional.of(mrAnna));

        // When
        FirestationCoverageDTO dto = service.getPersonsCoveredByStation(station);

        // Then
        assertNotNull(dto);
        assertEquals(3, dto.getCoveredPersons().size(),
                "Toutes les personnes des adresses couvertes doivent être listées");

        assertTrue(dto.getCoveredPersons().stream()
                .anyMatch(c -> "Mack".equals(c.getFirstName()) && "Miller".equals(c.getLastName())
                        && "1509 Culver St".equals(c.getAddress()) && "111-222".equals(c.getPhone())));
        assertTrue(dto.getCoveredPersons().stream()
                .anyMatch(c -> "Emma".equals(c.getFirstName()) && "Miller".equals(c.getLastName())
                        && "42 Roaling St".equals(c.getAddress()) && "333-444".equals(c.getPhone())));
        assertTrue(dto.getCoveredPersons().stream()
                .anyMatch(c -> "Anna".equals(c.getFirstName()) && "Miller".equals(c.getLastName())
                        && "42 Roaling St".equals(c.getAddress()) && "555-666".equals(c.getPhone())));

        assertEquals(1, dto.getNumberOfAdults(), "Un adulte attendu");
        assertEquals(2, dto.getNumberOfChildren(), "Deux enfants attendus");

        verify(dataRepository).getAddressesByStationNumber(station);
        verify(dataRepository).getPersonsByAddresses(addresses);
        verify(dataRepository, atLeast(3))
                .getMedicalRecordByFirstAndLastName(anyString(), anyString());
    }

    @Test
    void should_return_empty_result_when_no_address_is_covered() {
        // Given
        int station = 99;
        List<String> addresses = List.of();

        when(dataRepository.getAddressesByStationNumber(station))
                .thenReturn(addresses);
        when(dataRepository.getPersonsByAddresses(addresses))
                .thenReturn(List.of());

        // When
        FirestationCoverageDTO dto = service.getPersonsCoveredByStation(station);

        // Then
        assertNotNull(dto);
        assertTrue(dto.getCoveredPersons().isEmpty());
        assertEquals(0, dto.getNumberOfAdults());
        assertEquals(0, dto.getNumberOfChildren());

        verify(dataRepository).getAddressesByStationNumber(station);
        verify(dataRepository).getPersonsByAddresses(addresses);
        verify(dataRepository, never()).getMedicalRecordByFirstAndLastName(anyString(), anyString());
    }
}