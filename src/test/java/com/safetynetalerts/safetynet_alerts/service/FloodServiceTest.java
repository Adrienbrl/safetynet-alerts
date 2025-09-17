package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import com.safetynetalerts.safetynet_alerts.dto.FloodStationsResponseDTO;
import com.safetynetalerts.safetynet_alerts.dto.HouseholdDTO;
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
class FloodServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks FloodService service;

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

    private MedicalRecord mkMedicalRecordYearsOld(int years) {
        String birth = LocalDate.now().minusYears(years).format(BIRTH_FMT);
        MedicalRecord mr = new MedicalRecord();
        mr.setBirthdate(birth);
        mr.setMedications(List.of());
        mr.setAllergies(List.of());
        return mr;
    }

    // ---- TESTS ----

    @Test
    void should_build_households_by_stations_with_order_and_basic_fields() {
        // Given
        List<Integer> stations = List.of(3, 2);

        // 3 -> A1, A2 ; 2 -> A2, A3  (A2 est en doublon -> ordre final A1, A2, A3)
        when(dataRepository.getAddressesByStationNumber(3)).thenReturn(List.of("A1", "A2"));
        when(dataRepository.getAddressesByStationNumber(2)).thenReturn(List.of("A2", "A3"));

        Person mack = mkPerson("Mack", "Miller", "A1", "111");
        Person emma = mkPerson("Emma",  "Miller", "A2", "333");

        when(dataRepository.getPersonsByAddress("A1")).thenReturn(List.of(mack));
        when(dataRepository.getPersonsByAddress("A2")).thenReturn(List.of(emma));
        when(dataRepository.getPersonsByAddress("A3")).thenReturn(List.of());

        // Fallback MR
        when(dataRepository.getMedicalRecordByFirstAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // MRs minimaux (juste l’âge)
        when(dataRepository.getMedicalRecordByFirstAndLastName("Mack", "Miller"))
                .thenReturn(Optional.of(mkMedicalRecordYearsOld(35)));
        when(dataRepository.getMedicalRecordByFirstAndLastName("Emma", "Miller"))
                .thenReturn(Optional.of(mkMedicalRecordYearsOld(10)));

        // When
        FloodStationsResponseDTO resp = service.getHouseholdsByStations(stations);

        // Then – ordre des stations renvoyées tel quel
        assertEquals(stations, resp.getStations());

        // Ordre des adresses: A1, A2, A3
        List<String> addresses = resp.getHouseholds().stream().map(HouseholdDTO::getAddress).toList();
        assertEquals(List.of("A1", "A2", "A3"), addresses);

        // A1 -> John 35 ans
        var a1 = resp.getHouseholds().get(0);
        assertEquals(1, a1.getResidents().size());
        var rMack = a1.getResidents().get(0);
        assertEquals("Mack", rMack.getFirstName());
        assertEquals("111",  rMack.getPhone());
        assertEquals(35, rMack.getAge());

        // A2 -> Zoe 10 ans
        var a2 = resp.getHouseholds().get(1);
        assertEquals(1, a2.getResidents().size());
        var rEmma = a2.getResidents().get(0);
        assertEquals("Emma", rEmma.getFirstName());
        assertEquals("333", rEmma.getPhone());
        assertEquals(10, rEmma.getAge());

        // A3 -> personne
        var a3 = resp.getHouseholds().get(2);
        assertTrue(a3.getResidents().isEmpty());

        // Interactions clefs (on reste léger)
        verify(dataRepository).getAddressesByStationNumber(3);
        verify(dataRepository).getAddressesByStationNumber(2);
        verify(dataRepository).getPersonsByAddress("A1");
        verify(dataRepository).getPersonsByAddress("A2");
        verify(dataRepository).getPersonsByAddress("A3");
        verify(dataRepository, atLeast(2)).getMedicalRecordByFirstAndLastName(anyString(), anyString());
    }
}
