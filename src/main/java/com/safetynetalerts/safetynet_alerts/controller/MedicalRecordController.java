package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.MedicalRecordCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.MedicalRecord;
import com.safetynetalerts.safetynet_alerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/medicalRecord")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService service;

    @PostMapping
    public ResponseEntity<MedicalRecord> create(@Valid @RequestBody MedicalRecordCreateUpdateDTO dto) {
        try {
            MedicalRecord created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping
    public MedicalRecord update(@Valid @RequestBody MedicalRecordCreateUpdateDTO dto) {
        try {
            return service.update(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        try {
            service.delete(firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
