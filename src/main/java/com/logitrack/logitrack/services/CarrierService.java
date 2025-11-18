package com.logitrack.logitrack.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.dtos.CarrierRespDTO;
import com.logitrack.logitrack.mapper.CarrierMapper;
import com.logitrack.logitrack.models.Carrier;
import com.logitrack.logitrack.repositories.CarrierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    public CarrierRespDTO addCarrier(CarrierDTO carrierDTO) {
        Carrier carrier = carrierMapper.toEntity(carrierDTO);
        carrierRepository.save(carrier);
        return carrierMapper.toRespDTO(carrier);
    }

    public List<CarrierRespDTO> getAllCarriers() {
        List<Carrier> carriers = carrierRepository.findAll();
        return carriers.stream()
                .map(carrierMapper::toRespDTO)
                .toList();
    }

    public CarrierRespDTO getCarrierById(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrier with id " + id + " not found."));
        return carrierMapper.toRespDTO(carrier);
    }

    public CarrierRespDTO updateCarrier(UUID id,  CarrierDTO carrierDTO) {
        Carrier existingCarrier = carrierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrier with id " + id + " not found."));
        carrierMapper.updateCarrierFromDto(carrierDTO, existingCarrier);
        carrierRepository.save(existingCarrier);
        return carrierMapper.toRespDTO(existingCarrier);
    }

    public void deleteCarrierById(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrier with id " + id + " not found."));
        carrierRepository.delete(carrier);
    }
}
