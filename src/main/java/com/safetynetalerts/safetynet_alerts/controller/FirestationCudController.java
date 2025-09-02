package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.service.FirestationCudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/firestation")
@RequiredArgsConstructor
public class FirestationCudController {

    private final FirestationCudService service;

    @PostMapping
    public ResponseEntity<Firestation> create(@Valid @RequestBody FirestationCreateUpdateDTO dto) {
        try {
            Firestation created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping
    public Firestation update(@Valid @RequestBody FirestationCreateUpdateDTO dto) {
        try {
            return service.update(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer station) {

        if ((address == null && station == null) || (address != null && station != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide either 'address' or 'station'");
        }

        try {
            if (address != null) {
                service.deleteByAddress(address);
            } else {
                service.deleteByStation(station);
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
