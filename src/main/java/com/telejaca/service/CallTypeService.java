package com.telejaca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.telejaca.model.CallType;
import com.telejaca.repository.CallTypeRepository;

@Service
public class CallTypeService {

	@Autowired
	CallTypeRepository callTypeRepository;
	
	public List<CallType> getAllCallTypes(){
		return callTypeRepository.findAll();
	}
	
	public Optional<CallType> getCallTypeById(Integer id){
		return callTypeRepository.findById(id);
	}
	
	public CallType addCallType(CallType c) {
		CallType result = null;
		try {
			result = callTypeRepository.save(c);			
		}catch(Exception e) {
			
		}
		return result;
	}
	
	public void deleteCallType(CallType c) {
		try {
			callTypeRepository.delete(c);
		}catch(IllegalArgumentException e1) {
			throw e1;
		}catch(OptimisticLockingFailureException e2) {
			throw e2;
		}
	}
	
	public CallType editCallType(CallType c) {
		try {
			CallType editedCallType = callTypeRepository.save(c);
			return editedCallType;
		}catch(IllegalArgumentException e1) {
			throw e1;
		}catch(OptimisticLockingFailureException e2) {
			throw e2;
		}
	}
	
	public boolean callTypeExists(CallType callType) {
		return callTypeRepository.existsByDescription(callType.getDescription());
	}

}
