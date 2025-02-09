package com.telejaca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.telejaca.model.Medication;
import com.telejaca.model.Phone;
import com.telejaca.model.User;
import com.telejaca.repository.MedicationRepository;

@Service
public class MedicationService {

	@Autowired
	MedicationRepository medicationRepository;
	
	public List<Medication> getAllMedications(){
		return medicationRepository.findAll();
	}
	
	public Optional<Medication> getMedicationById(Integer id){
		return medicationRepository.findById(id);
	}
	
	public Medication addMedication(Medication m) {
		Medication result = null;
		try {
			result = medicationRepository.save(m);			
		}catch(Exception e) {
			
		}
		return result;
	}
	
	public void deleteMedication(Medication m) {
		try {
			medicationRepository.delete(m);
		}catch(IllegalArgumentException e1) {
			throw e1;
		}catch(OptimisticLockingFailureException e2) {
			throw e2;
		}
	}
	
	public Medication editMedication(Medication m) {
		try {
			Medication editedMedication = medicationRepository.save(m);
			return editedMedication;
		}catch(IllegalArgumentException e1) {
			throw e1;
		}catch(OptimisticLockingFailureException e2) {
			throw e2;
		}
	}
	
//	public List<Medication> getMedicationsByUser(Optional<User> user) {
//        if (user == null) {
//            throw new IllegalArgumentException("El usuario no puede ser nulo.");
//        }
//        return medicationRepository.findByMedicationUser(user);
//    }
	
	public boolean medicationExists(Medication medication) {
		return medicationRepository.existsByDescriptionAndUser(medication.getDescription(), medication.getUser());
	}

	public boolean isInteger(String id) {
		try {
			Integer.parseInt(id);
			return true;
		}catch(Exception e){
			return false;
		}
	}

}
