package com.telejaca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telejaca.model.CallType;

public interface CallTypeRepository extends JpaRepository<CallType, Integer> {
	
	@Override
	Optional<CallType> findById(Integer id);
	
	public List<CallType> findByDescriptionLike(String description);
	
	boolean existsByDescription(String description);
	
}
