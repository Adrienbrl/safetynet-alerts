package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirestationCudServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks FirestationCudService service;

    @Test
    void should_create_firestation_from_dto_and_pass_to_repository() {
        // Given
        var dto = new FirestationCreateUpdateDTO("1509 Culver St", 3);
        when(dataRepository.addFirestation(any(Firestation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // When
        Firestation saved = service.create(dto);

        // Then
        ArgumentCaptor<Firestation> captor = ArgumentCaptor.forClass(Firestation.class);
        verify(dataRepository).addFirestation(captor.capture());
        Firestation toAdd = captor.getValue();

        assertEquals("1509 Culver St", toAdd.getAddress());
        assertEquals(3, toAdd.getStation());
        assertSame(toAdd, saved);
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_update_station_by_address_using_repository_lambda() {
        // Given
        var dto = new FirestationCreateUpdateDTO("A", 7);
        when(dataRepository.updateFirestation(eq("A"), any()))
                .thenAnswer(inv -> {
                    String addr = inv.getArgument(0);
                    @SuppressWarnings("unchecked")
                    Consumer<Firestation> updater = inv.getArgument(1);
                    Firestation existing = new Firestation();
                    existing.setAddress(addr);
                    existing.setStation(1);
                    updater.accept(existing);
                    return existing;
                });

        // When
        Firestation updated = service.update(dto);

        // Then
        assertEquals("A", updated.getAddress());
        assertEquals(7, updated.getStation());
        verify(dataRepository).updateFirestation(eq("A"), any());
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void deleteByAddress_should_call_repo_and_not_throw_when_removed() {
        when(dataRepository.deleteFirestationByAddress("A")).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteByAddress("A"));

        verify(dataRepository).deleteFirestationByAddress("A");
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void deleteByAddress_should_throw_when_not_found() {
        when(dataRepository.deleteFirestationByAddress("A")).thenReturn(false);

        NoSuchElementException ex =
                assertThrows(NoSuchElementException.class, () -> service.deleteByAddress("A"));
        assertEquals("Mapping not found", ex.getMessage());

        verify(dataRepository).deleteFirestationByAddress("A");
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void deleteByStation_should_not_throw_when_count_positive() {
        when(dataRepository.deleteFirestationsByStation(3)).thenReturn(2);

        assertDoesNotThrow(() -> service.deleteByStation(3));

        verify(dataRepository).deleteFirestationsByStation(3);
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void deleteByStation_should_throw_when_count_zero() {
        when(dataRepository.deleteFirestationsByStation(3)).thenReturn(0);

        NoSuchElementException ex =
                assertThrows(NoSuchElementException.class, () -> service.deleteByStation(3));
        assertEquals("No mapping for station", ex.getMessage());

        verify(dataRepository).deleteFirestationsByStation(3);
        verifyNoMoreInteractions(dataRepository);
    }
}
