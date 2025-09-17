package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FireAddressResponseDTO;
import com.safetynetalerts.safetynet_alerts.dto.FireResidentDTO;
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
class FireServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks FireService service;

    // ---- HELPERS> ----

    private static Person mkPerson(String first, String last, String phone) {
        Person p = mock(Person.class);
        when(p.getFirstName()).thenReturn(first);
        when(p.getLastName()).thenReturn(last);
        when(p.getPhone()).thenReturn(phone);
        return p;
    }

    private static MedicalRecord mkMedicalRecordYearsOld(int years, List<String> meds, List<String> allergies) {
        LocalDate birth = LocalDate.now().minusYears(years);
        String birthStr = birth.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        MedicalRecord mr = mock(MedicalRecord.class);
        when(mr.getBirthdate()).thenReturn(birthStr);
        when(mr.getMedications()).thenReturn(meds);
        when(mr.getAllergies()).thenReturn(allergies);
        return mr;
    }

    // ---- TESTS ----

    @Test
    void should_return_empty_when_address_null_or_blank() {
        assertTrue(service.getFireInfoByAddress(null).isEmpty());
        assertTrue(service.getFireInfoByAddress("").isEmpty());
        assertTrue(service.getFireInfoByAddress("   ").isEmpty());
        verifyNoInteractions(dataRepository);
    }

    @Test
    void should_return_empty_when_station_not_found() {
        when(dataRepository.getStationNumberByAddress("Addr")).thenReturn(Optional.empty());

        assertTrue(service.getFireInfoByAddress("  Addr ").isEmpty());

        verify(dataRepository).getStationNumberByAddress("Addr"); // trim attendu
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_build_response_with_residents_when_station_found() {
        String addr = "1509 Culver St";
        when(dataRepository.getStationNumberByAddress(addr)).thenReturn(Optional.of(3));

        Person p1 = mkPerson("John", "Doe", "111-222");
        Person p2 = mkPerson("Anna", "Doe", "333-444");

        when(dataRepository.getPersonsByAddress(addr)).thenReturn(List.of(p1, p2));

        MedicalRecord mr1 = mkMedicalRecordYearsOld(35, List.of("aznol:200mg"), List.of("peanut"));
        when(dataRepository.getMedicalRecordByFirstAndLastName("John", "Doe"))
                .thenReturn(Optional.of(mr1));

        when(dataRepository.getMedicalRecordByFirstAndLastName("Anna", "Doe"))
                .thenReturn(Optional.empty());

        var opt = service.getFireInfoByAddress(addr);
        assertTrue(opt.isPresent());

        FireAddressResponseDTO resp = opt.get();
        assertEquals(List.of(3), resp.getStationNumbers());
        assertEquals(2, resp.getResidents().size());

        FireResidentDTO r1 = resp.getResidents().stream()
                .filter(r -> r.getFirstName().equals("John")).findFirst().orElseThrow();
        assertEquals("111-222", r1.getPhone());
        assertNotNull(r1.getAge());
        assertEquals(List.of("aznol:200mg"), r1.getMedications());
        assertEquals(List.of("peanut"), r1.getAllergies());

        FireResidentDTO r2 = resp.getResidents().stream()
                .filter(r -> r.getFirstName().equals("Anna")).findFirst().orElseThrow();
        assertEquals("333-444", r2.getPhone());
        assertNull(r2.getAge());
        assertNotNull(r2.getMedications());
        assertTrue(r2.getMedications().isEmpty());
        assertNotNull(r2.getAllergies());
        assertTrue(r2.getAllergies().isEmpty());

        verify(dataRepository).getStationNumberByAddress(addr);
        verify(dataRepository).getPersonsByAddress(addr);
        verify(dataRepository).getMedicalRecordByFirstAndLastName("John", "Doe");
        verify(dataRepository).getMedicalRecordByFirstAndLastName("Anna", "Doe");
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_use_empty_lists_when_meds_or_allergies_are_null() {
        String addr = "A";
        when(dataRepository.getStationNumberByAddress(addr)).thenReturn(Optional.of(1));

        Person p = mkPerson("Zoe", "Miller", "999");
        when(dataRepository.getPersonsByAddress(addr)).thenReturn(List.of(p));

        // Dossier présent mais lists null
        MedicalRecord mr = mkMedicalRecordYearsOld(12, null, null);
        when(dataRepository.getMedicalRecordByFirstAndLastName("Zoe", "Miller")).thenReturn(Optional.of(mr));

        var resp = service.getFireInfoByAddress(addr).orElseThrow();
        FireResidentDTO r = resp.getResidents().get(0);

        assertNotNull(r.getMedications());
        assertTrue(r.getMedications().isEmpty());
        assertNotNull(r.getAllergies());
        assertTrue(r.getAllergies().isEmpty());
    }

    @Test
    void should_return_response_with_empty_residents_when_station_found_but_no_persons() {
        String addr = "C";
        when(dataRepository.getStationNumberByAddress(addr)).thenReturn(Optional.of(5));
        when(dataRepository.getPersonsByAddress(addr)).thenReturn(List.of());

        var opt = service.getFireInfoByAddress(addr);
        assertTrue(opt.isPresent());
        assertEquals(List.of(5), opt.get().getStationNumbers());
        assertTrue(opt.get().getResidents().isEmpty());

        verify(dataRepository).getStationNumberByAddress(addr);
        verify(dataRepository).getPersonsByAddress(addr);
        verifyNoMoreInteractions(dataRepository);
    }
}
