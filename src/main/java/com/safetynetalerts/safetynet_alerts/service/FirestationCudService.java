package com.safetynetalerts.safetynet_alerts.service;

import com.safetynetalerts.safetynet_alerts.dto.FirestationCreateUpdateDTO;
import com.safetynetalerts.safetynet_alerts.model.Firestation;
import com.safetynetalerts.safetynet_alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FirestationCudService {

    private final DataRepository dataRepository;

    public Firestation create(FirestationCreateUpdateDTO dto) {
        Firestation f = toModel(dto);
        return dataRepository.addFirestation(f);
    }

    public Firestation update(FirestationCreateUpdateDTO dto) {
        return dataRepository.updateFirestation(dto.getAddress(), f -> f.setStation(dto.getStation()));
    }

    public void deleteByAddress(String address) {
        boolean removed = dataRepository.deleteFirestationByAddress(address);
        if (!removed) throw new NoSuchElementException("Mapping not found");
    }

    public void deleteByStation(int station) {
        int removed = dataRepository.deleteFirestationsByStation(station);
        if (removed == 0) throw new NoSuchElementException("No mapping for station");
    }

    private Firestation toModel(FirestationCreateUpdateDTO dto) {
        Firestation f = new Firestation();
        f.setAddress(dto.getAddress());
        f.setStation(dto.getStation());
        return f;
    }

}
