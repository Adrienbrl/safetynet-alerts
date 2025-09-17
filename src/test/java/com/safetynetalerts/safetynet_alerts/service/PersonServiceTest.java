package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.PersonCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Person;
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
class PersonServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks PersonService service;

    // ---- HELPERS ----

    private static PersonCreateUpdateDTO dto() {
        return new PersonCreateUpdateDTO(
                "John", "Doe", "1509 Culver St", "Culver", "97451", "841-874-6512", "john@ex.com"
        );
    }

    // ---- TESTS ----

    @Test
    void should_create_person_from_dto_and_pass_to_repository() {
        var dto = dto();

        when(dataRepository.addPerson(any(Person.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Person saved = service.create(dto);

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(dataRepository).addPerson(captor.capture());
        Person toAdd = captor.getValue();

        assertEquals("John", toAdd.getFirstName());
        assertEquals("Doe", toAdd.getLastName());
        assertEquals("1509 Culver St", toAdd.getAddress());
        assertEquals("Culver", toAdd.getCity());
        assertEquals("97451", toAdd.getZip());
        assertEquals("841-874-6512", toAdd.getPhone());
        assertEquals("john@ex.com", toAdd.getEmail());

        assertSame(toAdd, saved);
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void create_should_propagate_illegal_state_when_duplicate() {
        var dto = dto();
        when(dataRepository.addPerson(any(Person.class)))
                .thenThrow(new IllegalStateException("duplicate"));

        assertThrows(IllegalStateException.class, () -> service.create(dto));
        verify(dataRepository).addPerson(any(Person.class));
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_update_person_using_repository_lambda() {
        var dto = new PersonCreateUpdateDTO(
                "Jane", "Doe", "New Addr", "New City", "12345", "000-111", "jane@ex.com"
        );

        when(dataRepository.updatePerson(eq("Jane"), eq("Doe"), any()))
                .thenAnswer(inv -> {
                    // simulate existing person before update
                    Person existing = new Person();
                    existing.setFirstName("Jane");
                    existing.setLastName("Doe");
                    existing.setAddress("Old Addr");
                    existing.setCity("Old City");
                    existing.setZip("99999");
                    existing.setPhone("xxx");
                    existing.setEmail("old@ex.com");

                    @SuppressWarnings("unchecked")
                    Consumer<Person> updater = inv.getArgument(2);
                    updater.accept(existing); // apply lambda from service

                    return existing;
                });

        Person updated = service.update(dto);

        assertEquals("Jane", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("New Addr", updated.getAddress());
        assertEquals("New City", updated.getCity());
        assertEquals("12345", updated.getZip());
        assertEquals("000-111", updated.getPhone());
        assertEquals("jane@ex.com", updated.getEmail());

        verify(dataRepository).updatePerson(eq("Jane"), eq("Doe"), any());
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void delete_should_not_throw_when_removed_true() {
        when(dataRepository.deletePerson("John", "Doe")).thenReturn(true);

        assertDoesNotThrow(() -> service.delete("John", "Doe"));

        verify(dataRepository).deletePerson("John", "Doe");
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void delete_should_throw_when_person_not_found() {
        when(dataRepository.deletePerson("John", "Doe")).thenReturn(false);

        NoSuchElementException ex =
                assertThrows(NoSuchElementException.class, () -> service.delete("John", "Doe"));
        assertEquals("Person not found", ex.getMessage());

        verify(dataRepository).deletePerson("John", "Doe");
        verifyNoMoreInteractions(dataRepository);
    }
}

