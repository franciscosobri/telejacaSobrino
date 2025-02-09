package com.telejaca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telejaca.model.Phone;
import com.telejaca.model.User;

public interface PhoneRepository extends JpaRepository<Phone, Integer> {
	
	@Override
	Optional<Phone> findById(Integer id);
	
	List<Phone> findByNumberLike(String number);

	@Query("SELECT p FROM Phone p WHERE p.phoneUser = :user")
    List<Phone> findByPhoneUser(@Param("user") User user);
	
	@Query("SELECT p FROM Phone p JOIN p.phoneUser u ORDER BY u.surname ASC")
	List<Phone> findAllOrderedByUserSurname();
}
