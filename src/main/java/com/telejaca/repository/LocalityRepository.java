package com.telejaca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telejaca.model.Locality;

public interface LocalityRepository extends JpaRepository<Locality, Long> {
    Optional<Locality> findByName(String name);
}
