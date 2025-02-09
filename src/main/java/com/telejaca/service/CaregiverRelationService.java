package com.telejaca.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.telejaca.model.CaregiverRelation;
import com.telejaca.model.CaregiverRelationException;
import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.repository.CaregiverRelationRepository;

@Service
public class CaregiverRelationService {

	@Autowired
	CaregiverRelationRepository careRelationRepository;
	
	@Autowired
	UserService userService;
	
	public List<User> getRecipentUsersByCarerId(Integer id) throws Exception{
		try {
			//List<User> recipentUsersList = this.careRelationRepository.getRecipentUsersByCarerId(id);	
			List<User> recipentUsersList = this.careRelationRepository.getCaregiversListByRecipentUserId(id);
			if(recipentUsersList.isEmpty()) {
				throw new CaregiverRelationException("La persona usuaria no tiene usuarios bajo su cuidado.");
			}
			return recipentUsersList;
		}catch(Exception e) {
			throw e;
		}
	}
	
	public List<User> getCaregiverListByCareeId(Integer id) throws Exception{
		try {
			//List<User> recipentUsersList = this.careRelationRepository.getRecipentUsersByCarerId(id);	
			List<User> recipentUsersList = this.careRelationRepository.getRecipentUsersByCarerId(id);
			if(recipentUsersList.isEmpty()) {
				throw new CaregiverRelationException("La persona usuaria no cuidadores registrados en el sistema.");
			}
			return recipentUsersList;
		}catch(Exception e) {
			throw e;
		}
	}
	
	
	public Model getRelationsList(Model model, String id) {
		/* Validations */
		String error = "";
		Integer idInt = -1;
		if(id.isBlank()) {
			error = "Id de usuario vacía.";
		}else {
			try {
				idInt = userService.idIntoInt(id);
				Optional<User> user = userService.findUserById(idInt);

				User userValid = user.get();
				
				
				Integer userId = userValid.getId();
				model.addAttribute("userId", userId);
				if(userValid.getUserCaregiverRelation().isEmpty()) {
					model.addAttribute("caregiver_errorMsg", userValid.getName()+" no cuida de otros usuarios.");
				}else {
					model.addAttribute("caregiversRelation", userValid.getUserCaregiverRelation());
				}
				if(userValid.getUserCareeRelation().isEmpty()) {
					model.addAttribute("recipent_errorMsg", userValid.getName()+" no está bajo el cuidado de otros usuarios.");
				}else{
					model.addAttribute("recipentsCareRelation", userValid.getUserCareeRelation());
				}
				model.addAttribute("user", userValid);
			}catch (NoSuchElementException nse) {
				error = "Usuario no encontrado.";
			}catch(UserException ue) {
				error = ue.getMessage();
			}catch(NumberFormatException nfe) {
				error = nfe.getMessage();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(error.isEmpty()) {
			model.addAttribute("h1", "Relaciones de cuiaddo");
			//Tabla de usuarios cuidadores
			model.addAttribute("template", "caregiverRelation/caregiverList");
		}else {
			model.addAttribute("msg", error);
			model.addAttribute("template", "error");
		}		
		
		return model;
	}
	
	public boolean validateCaregiverObject(CaregiverRelation careRelation, User user) throws CaregiverRelationException {
		//Validamos que el usuario esté en la relación que se está introduciendo.
		if(!careRelation.getCaregiver().equals(user) && !careRelation.getRecipentUser().equals(user)) {
			throw new CaregiverRelationException("El usuario "+user.getName()+" "+user.getSurname()+" debe estar presente en la relación.");
		}if(careRelation.getCaregiver().equals(careRelation.getRecipentUser())) {
			throw new CaregiverRelationException("No se puede seleccionar al mismo usuario en los dos campos.");
		}
		return true;
	}
	
	/**This function returns the new relation saved or it will throw an CaregiverRelationException if the relation is already registered or if there was introduced the same user in both inputs*/
	public CaregiverRelation addCaregiverRelation(CaregiverRelation newCareRelation) throws Exception  {
		boolean relationExists = this.careRelationRepository.relationExists(newCareRelation.getCaregiver().getId(), newCareRelation.getRecipentUser().getId());
		if(relationExists) {
			throw new CaregiverRelationException("La relación ya está registrada en el sistema.");
		}else if(newCareRelation.getCaregiver().equals(newCareRelation.getRecipentUser())) {
			throw new CaregiverRelationException("No se puede seleccionar al mismo usuario en los dos campos.");
		}else {
			try {
				return this.careRelationRepository.save(newCareRelation);			
			}catch(Exception e) {
				throw e;
			}			
		}
	}
}
