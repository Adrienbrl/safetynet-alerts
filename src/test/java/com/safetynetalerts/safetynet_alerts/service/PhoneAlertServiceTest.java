package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhoneAlertServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks PhoneAlertService service;

    // ---- HELPERS ----

    private static Person mkPerson(String phone) {
        Person p = mock(Person.class);
        when(p.getPhone()).thenReturn(phone);
        return p;
    }

    // ---- TESTS ----

    @Test
    void should_return_empty_list_when_station_non_positive() {
        assertTrue(service.getPhonesByStation(0).isEmpty());
        assertTrue(service.getPhonesByStation(-5).isEmpty());
        verifyNoInteractions(dataRepository);
    }

    @Test
    void should_return_empty_list_when_no_addresses_for_station() {
        when(dataRepository.getAddressesByStationNumber(1)).thenReturn(List.of());

        var res = service.getPhonesByStation(1);

        assertTrue(res.isEmpty());
        verify(dataRepository).getAddressesByStationNumber(1);
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_return_sorted_unique_trimmed_nonblank_phones() {
        int station = 2;
        List<String> addresses = List.of("A1", "A2");
        when(dataRepository.getAddressesByStationNumber(station)).thenReturn(addresses);

        // Téléphones : null, blancs, espaces à trim, doublons, ordre quelconque
        var persons = List.of(
                mkPerson("  222  "),
                mkPerson(null),
                mkPerson(""),
                mkPerson("111"),
                mkPerson(" "),
                mkPerson("333"),
                mkPerson("111") // doublon
        );
        when(dataRepository.getPersonsByAddresses(addresses)).thenReturn(persons);

        var res = service.getPhonesByStation(station);

        assertEquals(List.of("111", "222", "333"), res, "tri ascendant + distinct + trim + filtre blancs/nulls");

        // Vérifie qu'on a bien appelé le repo avec la même liste d'adresses
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataRepository).getAddressesByStationNumber(station);
        verify(dataRepository).getPersonsByAddresses(captor.capture());
        assertEquals(addresses, captor.getValue());
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_return_empty_list_when_addresses_exist_but_no_persons() {
        when(dataRepository.getAddressesByStationNumber(3)).thenReturn(List.of("X"));
        when(dataRepository.getPersonsByAddresses(List.of("X"))).thenReturn(List.of());

        var res = service.getPhonesByStation(3);

        assertTrue(res.isEmpty());
        verify(dataRepository).getAddressesByStationNumber(3);
        verify(dataRepository).getPersonsByAddresses(List.of("X"));
        verifyNoMoreInteractions(dataRepository);
    }
}
