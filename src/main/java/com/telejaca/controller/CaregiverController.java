package com.telejaca.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telejaca.model.CaregiverRelation;
import com.telejaca.model.CaregiverRelationException;
import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.service.CaregiverRelationService;
import com.telejaca.service.UserService;

@Controller
public class CaregiverController {

	@Autowired
	CaregiverRelationService caregiverService;
	
	@Autowired
	UserService userService;
//	
//	@GetMapping("/caregivers/add")
//	public String getCaregiversUserAdd(Model model) {
//		model.addAttribute("listUser", model);
//		model.addAttribute("objeto", new Caregiver());
//		
//	}
	
	@GetMapping("/caregiversList/{id}")
	public String getCaregiversUserList(Model model, @PathVariable String id) {
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
			return "caregiverRelation/caregiverList";
		}else {
			model.addAttribute("msg", error);
			return "error";
		}		
	}

	@GetMapping("/relation/add")
	public String addCaregiverRelation(Model model) {
		try {
			List<User> usersList = this.userService.getAllActiveUsersOrderedBySurname();	
			model.addAttribute("usersList", usersList);
			model.addAttribute("CaregiverRelation", new CaregiverRelation());
		}catch(UserException ue) {
			model.addAttribute("errorMsg", ue.getMessage());
		}			
	return "caregiverRelation/caregiverForm";
}	
	
	@PostMapping("/caregiverRelation/add")
	public String addCaregiverRelation(Model model, @Validated @ModelAttribute("CaregiverRelation") CaregiverRelation careRelation, BindingResult bindingResult) {
		
		boolean added = false;
		
		if(!bindingResult.hasErrors()) {
			try {
				careRelation = this.caregiverService.addCaregiverRelation(careRelation);
				String confirmMsg = "Relación entre "+careRelation.getCaregiver().getName()+" "+careRelation.getCaregiver().getSurname()+" y "+careRelation.getRecipentUser().getName()+" "+careRelation.getRecipentUser().getSurname()+" añadida con éxito";
				model.addAttribute("msg", confirmMsg);
			}catch(CaregiverRelationException cre) {
				model.addAttribute("errorMsg", cre.getMessage());
				cre.printStackTrace();
			}catch(Exception e) {
				model.addAttribute("errorMsg", "Error al añdir al usuario, si el problema persiste, contacte con su administrador. ");
				e.printStackTrace();
			}
		}
		
			try {
				List<User> usersList = this.userService.getAllActiveUsersOrderedBySurname();	
				model.addAttribute("usersList", usersList);
			}catch(UserException ue) {
				model.addAttribute("errorMsg", ue.getMessage());
				ue.printStackTrace();
			}			
		
		return "caregiverRelation/caregiverForm";
	}
	
	
	// Vista de detalle de usuario
	
		// Validación común
		private User validateAndGetUser(String id, Model model) throws UserException {
		    if (id == null || id.isBlank()) {
		        model.addAttribute("msg", "Id de usuario vacía.");
		        return null;
		    }

		    try {
		        Integer idInt = userService.idIntoInt(id);
		        Optional<User> userOpt = userService.findUserById(idInt);
		        if (userOpt.isEmpty()) {
		            model.addAttribute("msg", "Usuario no encontrado.");
		            return null;
		        }
		        return userOpt.get();
		    } catch (NumberFormatException e) {
		        model.addAttribute("msg", "Id de usuario inválida.");
		        return null;
		    }
		}
		
		// Configurar vista común
		private void setupUserModel(Model model, User user, String activity, boolean enable, String h1, String action) {
		    model.addAttribute("user", user);
		    model.addAttribute("activity", activity);
		    model.addAttribute("enable", enable);
		    model.addAttribute("h1", h1);
		    model.addAttribute("action", action);
		    
			String localityName = "";
			if(user.getLocality()!=null) {
				localityName = user.getLocality().getName()==null ? "Sin registro" : user.getLocality().getName();				
			}		    
			model.addAttribute("localityName", localityName);
		    
		    if(user.getUserCaregiverRelation().isEmpty()) {
				model.addAttribute("caregiver_errorMsg", user.getName()+" no cuida de otros usuarios.");
			}else {
				model.addAttribute("caregiversRelation", user.getUserCaregiverRelation());
			}
			if(user.getUserCareeRelation().isEmpty()) {
				model.addAttribute("recipent_errorMsg", user.getName()+" no está bajo el cuidado de otros usuarios.");
			}else{
				model.addAttribute("recipentsCareRelation", user.getUserCareeRelation());
			}

		    model.addAttribute("showRelations", true);
		}
		
		// Manejar errores
		private String handleError(Model model, String errorMessage, String viewName) {
		    model.addAttribute("msg", errorMessage);
		    return viewName;
		}
		
		@GetMapping("/user/{id}/careGivers")
		public String getUserCaregiversList(Model model, @PathVariable String id) throws UserException {
			User user = validateAndGetUser(id, model);
		    if (user == null) {
		        return handleError(model, "Error al obtener el usuario.", "error");
		    }

		    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
		    model.addAttribute("disabled", true);
		    return "user/userForm";
		}
	
	//Comentado: otro método diseñado para añadir relación de un usuario concreto
//	@GetMapping("/user/{id}/addRelation")
//	public String addCaregiverRelation(Model model, @PathVariable String id) {
//		//Validamos id
//		String idError = "";
//			if(id.isBlank()) {
//				idError = "Id de usuario vacía.";
//			}else {
//				try {
//					Integer idInt = userService.idIntoInt(id);
//					Optional<User> userDataBase = userService.findUserById(idInt);
//					
//					User userValid = userDataBase.get();
//					
//					Integer userId = userValid.getId();
//					
//					model.addAttribute("user", userValid);
//					model.addAttribute("userId", userId);
//					
//				}catch (NoSuchElementException nse) {
//					idError = "Usuario no encontrado.";
//				}catch(UserException ue) {
//					idError = ue.getMessage();
//				}catch(NumberFormatException ue) {
//					idError = ue.getMessage();
//				}
//			}		
		
//		else {
//			Integer userId = user.getId();
//			model.addAttribute("userId", userId);
//		}
		
		// If there are errors, it will send to the error page. Otherwise, it will show the form with the user information.
//		if(idError.isEmpty()) {
//			try {
//				List<User> usersList = this.userService.getAllActiveUsers();	
//				model.addAttribute("usersList", usersList);
//				model.addAttribute("CaregiverRelation", new CaregiverRelation());
//			}catch(UserException ue) {
//				model.addAttribute("errorMsg", ue.getMessage());
//			}			
//		}else {
//			model.addAttribute("errorMsg", idError);
//		}
//		return "caregiverRelation/caregiverForm";
//	}*/
	
//	@PostMapping("/caregiverRelation/add")
//	public String addCaregiverRelation(Model model, @Validated @ModelAttribute("CaregiverRelation") CaregiverRelation careRelation, BindingResult bindingResult,  @RequestParam("userId") Integer userId) {
//		
//		boolean added = false;
//		
//		if(!bindingResult.hasErrors()) {
//			try {
//				careRelation = this.caregiverService.addCaregiverRelation(careRelation);
//				added=true;
//			}catch(CaregiverRelationException cre) {
//				model.addAttribute("errorMsg", cre.getMessage());
//				cre.printStackTrace();
//			}catch(Exception e) {
//				model.addAttribute("errorMsg", "Error al añdir al usuario, si el problema persiste, contacte con su administrador. ");
//				e.printStackTrace();
//			}
//		}
//		
//		try {
//			Optional<User> userDataBase = userService.findUserById(userId);
//			User userValid = userDataBase.get();
//			model.addAttribute("user", userValid);
//		}catch (NoSuchElementException nse) {
//			model.addAttribute("errorMsg", "Usuario no encontrado.");
//		}catch(UserException ue) {
//			model.addAttribute("errorMsg", ue.getMessage());
//		}
//		
//		if(added) {
//			model.addAttribute("msg", "Relación añadida con éxito");
//			
//			return "user/userForm";
//		}else {
//			try {
//				List<User> usersList = this.userService.getAllActiveUsers();	
//				model.addAttribute("usersList", usersList);
//			}catch(UserException ue) {
//				model.addAttribute("errorMsg", ue.getMessage());
//				ue.printStackTrace();
//			}			
//			return "caregiverRelation/caregiverForm";
//		}
//		
//	}
//	@PostMapping("/caregiverRelation/add")
//	public String addCaregiverRelation(Model model, @Validated @ModelAttribute("CaregiverRelation") CaregiverRelation careRelation, BindingResult bindingResult,  @RequestParam("userId") Integer userId) {
//		
//		boolean added = false;
//		
//		if(!bindingResult.hasErrors()) {
//			try {
//				this.caregiverService.validateCaregiverObject(careRelation, user);
//				careRelation = this.caregiverService.addCaregiverRelation(careRelation);
//				added=true;
//			}catch(CaregiverRelationException cre) {
//				model.addAttribute("errorMsg", cre.getMessage());
//				cre.printStackTrace();
//			}catch(Exception e) {
//				model.addAttribute("errorMsg", "Error al añdir al usuario, si el problema persiste, contacte con su administrador. ");
//				e.printStackTrace();
//			}
//		}
//		
//		Optional<User> userDataBase = userService.findUserById(userId);
//		
//		User userValid = userDataBase.get();
//		
//		if(added) {
//			model.addAttribute("msg", "Relación añadida con éxito");
//			
//			return "user/userForm";
//		}else {
//			try {
//				List<User> usersList = this.userService.getAllActiveUsers();	
//				model.addAttribute("usersList", usersList);
//			}catch(UserException ue) {
//				model.addAttribute("errorMsg", ue.getMessage());
//				ue.printStackTrace();
//			}			
//			model.addAttribute("user", user);
//			return "caregiverRelation/caregiverForm";
//		}
//		
//	}
	
//	@GetMapping("/caregiversList/{id}")
//	public String getCaregiversUserList(Model model, @PathVariable String id) {
//		
//		/* Validations */
//		String error = "";
//		Integer idInt = -1;
//		if(id.isBlank()) {
//			error = "Id de usuario vacía.";
//		}else {
//			try {
//				idInt = userService.idIntoInt(id);
//				Optional<User> user = userService.findUserById(idInt);
//				if(user.orElse(null)==null) {
//					error = "Usuario no encontrado.";
//				}else {
//					model.addAttribute("user", user);
//					Integer userId = user.orElse(null).getId();
//					model.addAttribute("userId", userId);
//				}
//			}catch(NumberFormatException ue) {
//				error = ue.getMessage();
//			}
//		}
//		if(error.isEmpty()) {
//			model.addAttribute("h1", "Relaciones de cuiaddo");
//			//Tabla de usuarios cuidadores
//			try {
//				List<User> recipentUsersList= caregiverService.getRecipentUsersByCarerId(idInt);	
//				model.addAttribute("recipentUsersList", recipentUsersList);
//			}catch(CaregiverRelationException ce) {
//				model.addAttribute("recipent_errorMsg", ce.getMessage());
//			}catch(Exception e) {
//				model.addAttribute("recipent_errorMsg", "Error al buscar en la lista de cuidadores.");
//				e.printStackTrace();
//			}
//			//Tabla de usuarios bajo cuidado
//			try {
//				List<User> careersList= caregiverService.getCaregiverListByCareeId(idInt);	
//				model.addAttribute("caregiversList", careersList);
//			}catch(CaregiverRelationException ce) {
//				model.addAttribute("caregivers_errorMsg", ce.getMessage());
//			}catch(Exception e) {
//				model.addAttribute("caregivers_errorMsg", "Error al buscar en la lista de cuidadores.");
//				e.printStackTrace();
//			}
//			return "caregiverRelation/caregiverList";
//		}else {
//			model.addAttribute("msg", error);
//			return "error";
//		}		
//	}
}
