package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.model.Person;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityEmailServiceTest {

    @Mock DataRepository dataRepository;
    @InjectMocks CommunityEmailService service;

    // ---- HELPERS ----

    private static Person mkPerson(String email) {
        Person p = mock(Person.class);
        when(p.getEmail()).thenReturn(email);
        return p;
    }

    // ---- TESTS ----

    @Test
    void should_return_empty_list_when_city_is_null_or_blank() {
        assertTrue(service.getEmailsByCity(null).isEmpty());
        assertTrue(service.getEmailsByCity("").isEmpty());
        assertTrue(service.getEmailsByCity("   ").isEmpty());

        verifyNoInteractions(dataRepository);
    }

    @Test
    void should_return_sorted_and_cleaned_emails() {
        String city = "Culver";
        List<Person> persons = List.of(
                mkPerson("emma@email.com"),
                mkPerson("   anna@email.com   "), // doit être trim
                mkPerson(null),                    // ignoré
                mkPerson(" "),                     // ignoré (blank)
                mkPerson("bob@email.com")
        );

        when(dataRepository.getPersonsByCity("Culver")).thenReturn(persons);

        List<String> result = service.getEmailsByCity(city);

        assertEquals(3, result.size());
        assertEquals(List.of("anna@email.com", "bob@email.com", "emma@email.com"), result);
        verify(dataRepository).getPersonsByCity("Culver");
        verifyNoMoreInteractions(dataRepository);
    }

    @Test
    void should_return_empty_list_when_no_persons_in_city() {
        when(dataRepository.getPersonsByCity("Nowhere")).thenReturn(List.of());

        List<String> result = service.getEmailsByCity("Nowhere");

        assertTrue(result.isEmpty());
        verify(dataRepository).getPersonsByCity("Nowhere");
        verifyNoMoreInteractions(dataRepository);
    }
}
