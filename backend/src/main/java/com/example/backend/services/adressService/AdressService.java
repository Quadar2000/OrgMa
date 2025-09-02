package com.example.backend.services.adressService;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.example.backend.entities.adress.Adress;
import com.example.backend.repositories.adressRepository.AdressRepository;

@Service
public class AdressService {

    private final AdressRepository adressRepository;

    public AdressService(AdressRepository adressRepository) {
        this.adressRepository = adressRepository;
    }

    public Adress findOrAddAdress(String street, String buildingNumber, String premisesNumber, String code, String city, String region, String country) {
        Adress probe = new Adress();
        probe.setStreet(street);
        probe.setBuildingNumber(buildingNumber);
        probe.setPremisesNumber(premisesNumber);
        probe.setCode(code);
        probe.setCity(city);
        probe.setRegion(region);
        probe.setCountry(country);

        return adressRepository.findOne(Example.of(probe)).orElseGet(() -> adressRepository.save(probe));
    }
}
