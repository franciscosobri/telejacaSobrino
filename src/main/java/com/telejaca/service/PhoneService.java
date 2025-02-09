package com.telejaca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.telejaca.model.Phone;
import com.telejaca.model.User;
import com.telejaca.repository.PhoneRepository;

@Service
public class PhoneService {

	@Autowired
	PhoneRepository phoneRepository;
	
	
	public List<Phone> getAllPhones(){
		return phoneRepository.findAllOrderedByUserSurname();
	}
	
	public List<Phone> searchUserByNumber(String number){
		return phoneRepository.findByNumberLike(number);
	}
	
	public List<Phone> getPhonesByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        return phoneRepository.findByPhoneUser(user);
    }

	public Phone addPhone(Phone p) {
		Phone result = null;
		try {
			result = phoneRepository.saveAndFlush(p);			
		}catch(Exception e) {
			
		}
		return result;
	}
	
	public boolean isInteger(String id) {
		try {
			Integer.parseInt(id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Phone getPhoneById(Integer id) {
		Optional<Phone> phone = phoneRepository.findById(id);	
		return phone.orElse(null);
	}

	public void deletePhone(Phone p) {
		try {
			phoneRepository.delete(p);
		}catch(IllegalArgumentException e1) {
			throw e1;
		}catch(OptimisticLockingFailureException e2) {
			throw e2;
		}
	}
	
	public Phone editPhone(Phone p) {
		try {
			Phone phoneSaved = phoneRepository.save(p);
			return phoneSaved;
		}catch(IllegalArgumentException e1) {
			throw e1;
		}catch(OptimisticLockingFailureException e2) {
			throw e2;
		}
	}

}
