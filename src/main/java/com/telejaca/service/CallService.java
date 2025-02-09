package com.telejaca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.telejaca.model.Call;
import com.telejaca.model.CallException;
import com.telejaca.model.CallType;
import com.telejaca.model.Employee;
import com.telejaca.model.User;
import com.telejaca.repository.CallRepository;
import com.telejaca.repository.CallTypeRepository;

@Service
public class CallService {

	@Autowired
	CallRepository callRepository;
	

	public Call getHigherOrderCall(Integer userId, String emplUsername, Integer callTypeId, LocalDate date) throws CallException {
		Optional<Call> optCall = this.callRepository.getHigherOrderCall(userId, emplUsername, callTypeId, date);
		try {
			Call higherCall = optCall.orElseThrow();
			return higherCall;
		}catch(NoSuchElementException nsee) {
			throw new CallException("Llamada no encontrada");
		}
	}
//	/**This function will return a Call if it is valid to edit. This means that it has to exists in the database*/
//	public Call isCallEditable(Call call) {
//		
//	} 
	
//	public List<Call> getCallsByUser(User user) {
//	    return callRepository.findByUser(user);
//	}
	
	public List<Call> getLatestCallsByUser(User user) {
	    return callRepository.findLatestCallsByUser(user.getId());
	}
	
	public List<Call> findByUserAndEmployee(User user, Employee employee) {
	    return callRepository.findByUserAndEmployee(user, employee);
	}

	public boolean callAlreadyExists(Call call) {
		return this.callRepository.existsByUserAndEmployeeAndCallTypeAndDate(call.getUser(), call.getEmployee(), call.getCallType(), call.getDate());
	}
	public Call editCall(Model model, Call callEdited) {
		callEdited.setOrder(callEdited.getOrder()+1);
		try {
			Call callsaved = this.savecall(callEdited);		
			model.addAttribute("call", callsaved);
			model.addAttribute("successCallMsg", "Llamada modificada con éxito.");				
		}catch(CallException ce) {
			model.addAttribute("errorCallMsg", ce.getMessage());
		}
		return callEdited;
	}
	
	public Call savecall(Call call) throws CallException {
		try {
			Call callSaved = this.callRepository.saveAndFlush(call);	
			return callSaved;
		}catch(Exception e) {
			e.printStackTrace();
			throw new CallException("Error al guardar la llamada. Inténtelo de nuevo o más tarde. Si el problema persiste, contacte con su administrador.");
		}
	}
	
	public void getHtmlContentForUserForm(Model model, User user) {
		model.addAttribute("h1", "Detalles del usuario");
		model.addAttribute("disabled", true);
		model.addAttribute("activity", "show");
		String localityName = "";
		if(user.getLocality()!=null) {
			localityName = user.getLocality().getName()==null ? "Sin registro" : user.getLocality().getName();				
		}
		model.addAttribute("localityName", localityName);
	}
}
