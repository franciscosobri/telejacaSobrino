package com.telejaca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telejaca.model.Medication;
import com.telejaca.model.User;

public interface MedicationRepository extends JpaRepository<Medication, Integer> {
	
	@Override
	Optional<Medication> findById(Integer id);
	
	boolean existsByDescriptionAndUser(String description, User user);
	
}
