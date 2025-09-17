package com.safetynetalerts.safetynet_alerts.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    private static final DateTimeFormatter US = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Test
    void calculateAge_invalidFormat_throws() {
        // format ISO et non MM/dd/yyyy
        assertThrows(DateTimeParseException.class, () -> DateUtils.calculateAge("2000-12-31"));
    }

    @Test
    void calculateAge_nonexistentMonth_throws() {
        // 13e mois → invalide
        assertThrows(DateTimeParseException.class, () -> DateUtils.calculateAge("13/10/2000"));
    }

    @Test
    void calculateAge_futureBirthdate_throws() {
        // Date future (toujours future quel que soit le jour d'exécution)
        String future = US.format(LocalDate.now().plusYears(1));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.calculateAge(future));
    }
}
