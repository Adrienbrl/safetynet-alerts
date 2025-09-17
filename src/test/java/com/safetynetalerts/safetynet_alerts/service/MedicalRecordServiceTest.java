package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.MedicalRecordCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks MedicalRecordService service;

    // ---- HELPERS ----

    private static MedicalRecordCreateUpdateDTO dto(
            String first, String last, String birth, List<String> meds, List<String> allergies) {
        return new MedicalRecordCreateUpdateDTO(first, last, birth, meds, allergies);
    }

    // ---- TESTS ----

    @Test
    void should_create_medical_record_from_dto_and_pass_to_repository() {
        // Given
        var dto = dto("John", "Doe", "01/02/1990", List.of("aznol:200mg"), List.of("peanut"));

        when(dataRepository.addMedicalRecord(any(MedicalRecord.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // When
        MedicalRecord saved = service.create(dto);

        // Then
        ArgumentCaptor<MedicalRecord> captor = ArgumentCaptor.forClass(MedicalRecord.class);
        verify(dataRepository).addMedicalRecord(captor.capture());
        MedicalRecord toAdd = captor.getValue();

        assertEquals("John", toAdd.getFirstName());
        assertEquals("Doe", toAdd.getLastName());
        assertEquals("01/02/1990", toAdd.getBirthdate());
        assertEquals(List.of("aznol:200mg"), toAdd.getMedications());
        assertEquals(List.of("peanut"), toAdd.getAllergies());

        assertSame(toAdd, saved);
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_update_existing_record_using_repository_lambda() {
        // Given
        var dto = dto("Jane", "Doe", "12/31/2000", List.of(), List.of("pollen"));

        when(dataRepository.updateMedicalRecord(eq("Jane"), eq("Doe"), any()))
                .thenAnswer(inv -> {
                    // Simule un enregistrement existant
                    MedicalRecord existing = new MedicalRecord();
                    existing.setFirstName("Jane");
                    existing.setLastName("Doe");
                    existing.setBirthdate("01/01/1999");
                    existing.setMedications(List.of("old:10mg"));
                    existing.setAllergies(List.of("dust"));

                    @SuppressWarnings("unchecked")
                    Consumer<MedicalRecord> updater = inv.getArgument(2);
                    updater.accept(existing); // applique les setters définis dans le service

                    return existing;
                });

        // When
        MedicalRecord updated = service.update(dto);

        // Then
        assertEquals("Jane", updated.getFirstName()); // noms conservés
        assertEquals("Doe", updated.getLastName());
        assertEquals("12/31/2000", updated.getBirthdate());
        assertEquals(List.of(), updated.getMedications());
        assertEquals(List.of("pollen"), updated.getAllergies());

        verify(dataRepository).updateMedicalRecord(eq("Jane"), eq("Doe"), any());
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void delete_should_call_repo_and_not_throw_when_found() {
        when(dataRepository.deleteMedicalRecord("John", "Doe")).thenReturn(true);

        assertDoesNotThrow(() -> service.delete("John", "Doe"));

        verify(dataRepository).deleteMedicalRecord("John", "Doe");
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void delete_should_throw_when_record_not_found() {
        when(dataRepository.deleteMedicalRecord("John", "Doe")).thenReturn(false);

        NoSuchElementException ex =
                assertThrows(NoSuchElementException.class, () -> service.delete("John", "Doe"));
        assertEquals("Medical record not found", ex.getMessage());

        verify(dataRepository).deleteMedicalRecord("John", "Doe");
        verifyNoMoreInteractions(dataRepository);
    }
}

