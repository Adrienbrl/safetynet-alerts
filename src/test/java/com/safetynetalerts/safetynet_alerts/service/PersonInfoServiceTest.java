package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.PersonInfoDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonInfoServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks PersonInfoService service;

    // ---- HELPERS ----

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private Person mkPerson(String first, String last, String address, String email) {
        Person p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(address);
        p.setEmail(email);
        return p;
    }

    private MedicalRecord mkMRYearsOld(int years) {
        String birth = LocalDate.now().minusYears(years).format(FMT);
        MedicalRecord mr = new MedicalRecord();
        mr.setBirthdate(birth);
        mr.setMedications(List.of()); // vide par défaut
        mr.setAllergies(List.of());
        return mr;
    }

    // ---- TESTS ----

    @Test
    void should_return_basic_info_and_handle_missing_medical_record() {
        // Given
        String raw = "  Doe  "; // on veut vérifier le trim
        Person john = mkPerson("John", "Doe", "A1", "john@ex.com");
        Person anna = mkPerson("Anna", "Doe", "A2", "anna@ex.com");

        when(dataRepository.getPersonsByLastName("Doe"))
                .thenReturn(List.of(john, anna));

        // Prépare les MRs
        MedicalRecord mrJohn = mkMRYearsOld(30);

        when(dataRepository.getMedicalRecordByFirstAndLastName("John", "Doe"))
                .thenReturn(Optional.of(mrJohn));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Anna", "Doe"))
                .thenReturn(Optional.empty());

        // When
        List<PersonInfoDTO> res = service.getPersonsInfoByLastName(raw);

        // Then
        assertEquals(2, res.size());

        PersonInfoDTO johnDTO = res.stream()
                .filter(p -> p.getFirstName().equals("John")).findFirst().orElseThrow();
        assertEquals("Doe", johnDTO.getLastName());
        assertEquals("A1", johnDTO.getAddress());
        assertEquals("john@ex.com", johnDTO.getEmail());
        assertEquals(30, johnDTO.getAge());
        assertNotNull(johnDTO.getMedications());
        assertNotNull(johnDTO.getAllergies());

        PersonInfoDTO annaDTO = res.stream()
                .filter(p -> p.getFirstName().equals("Anna")).findFirst().orElseThrow();
        assertEquals("A2", annaDTO.getAddress());
        assertEquals("anna@ex.com", annaDTO.getEmail());
        assertEquals(0, annaDTO.getAge());
        assertNotNull(annaDTO.getMedications());
        assertTrue(annaDTO.getMedications().isEmpty());
        assertNotNull(annaDTO.getAllergies());
        assertTrue(annaDTO.getAllergies().isEmpty());

        // Interactions minimales (on vérifie le trim + les 2 lectures MR)
        verify(dataRepository).getPersonsByLastName("Doe");
        verify(dataRepository).getMedicalRecordByFirstAndLastName("John", "Doe");
        verify(dataRepository).getMedicalRecordByFirstAndLastName("Anna", "Doe");
    }


    @Test
    void should_return_empty_when_no_persons_found() {
        when(dataRepository.getPersonsByLastName("Smith")).thenReturn(List.of());

        List<PersonInfoDTO> res = service.getPersonsInfoByLastName("Smith");

        assertTrue(res.isEmpty());
        verify(dataRepository).getPersonsByLastName("Smith");
        verifyNoMoreInteractions(dataRepository);
    }
}
