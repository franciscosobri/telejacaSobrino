package com.telejaca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telejaca.model.Locality;
import com.telejaca.repository.LocalityRepository;

@Service
public class LocalityService {

    @Autowired
    private LocalityRepository localityRepository;

    public List<Locality> getAllLocalitys() {
        return localityRepository.findAll();
    }

    public void saveLocality(Locality locality) {
        localityRepository.save(locality);
    }

    public Optional<Locality> getLocalityById(Long id) {
        return localityRepository.findById(id);
    }

    public Optional<Locality> getLocalityByName(String name) {
        return localityRepository.findByName(name);
    }

    public void updateLocality(Locality locality) {
        Locality existingLocality = localityRepository.findById(locality.getId()).orElse(null);
        if (existingLocality != null) {
            existingLocality.setName(locality.getName());
            localityRepository.save(existingLocality);
        }
    }

    public void deleteLocality(Long id) {
        localityRepository.deleteById(id);
    }

    public boolean isInteger(String word) {
        try {
            Integer.parseInt(word);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
