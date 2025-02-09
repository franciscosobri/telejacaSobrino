package com.telejaca.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.telejaca.model.Call;
import com.telejaca.model.CallException;
import com.telejaca.model.CallType;
import com.telejaca.model.Employee;
import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.service.CallService;
import com.telejaca.service.CallTypeService;
import com.telejaca.service.EmployeeService;
import com.telejaca.service.LocalityService;
import com.telejaca.service.UserService;

@Controller
public class CallController {
	
	/*Añadir el Autowired */
	@Autowired
	CallService callService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LocalityService localityService;

	@Autowired
	CallTypeService callTypeService;
	
	@Autowired
	EmployeeService employeeService;
	
	// Mostrar lista de teléfonos asociados al usuario
	@GetMapping("/user/{id}/calls")
	public String listCalls(Model model, @PathVariable String id) throws UserException {
		/* Validations */
		String error = "";
		User user = new User();

		if(id.isBlank()) {
			error = "Id de usuario vacía.";
		}else {
			try {
				Integer idInt = userService.idIntoInt(id);
				Optional<User> userOpt = userService.findUserById(idInt);
				if(userOpt.orElse(null)==null) {
					error = "Usuario no encontrado.";
				}else {
					user = userOpt.get();
					
					model.addAttribute("user", user);
					Integer userId = userOpt.orElse(null).getId();
					model.addAttribute("userId", userId);
					
					List<Call> calls = callService.getLatestCallsByUser(user);
	                model.addAttribute("calls", calls);
				}
			}catch(NumberFormatException ue) {
				error = ue.getMessage();
			}
		}
		/* If there are errors, it will send to the error page. Otherwise, it will show the form with the user information.*/
		if(error.isEmpty()) {
			/* HTML content */
			model.addAttribute("h1", "Detalles del usuario");
			model.addAttribute("disabled", true);
			model.addAttribute("action", "");
			model.addAttribute("activity", "show");
			String localityName = "";
			if(user.getLocality()!=null) {
				localityName = user.getLocality().getName()==null ? "Sin registro" : user.getLocality().getName();				
			}
			model.addAttribute("localityName", localityName);
			
			
			// TODO - AÑADIR EL MÉTODO PARA RECUPERAR EL HISTORIAL DE LLAMADAS 
			
//			List<Phone> calls = callService.getCallsByUser(user);
//			model.addAttribute("calls", calls);
			
			model.addAttribute("showCalls", true); // Indicador para mostrar la lista de teléfonos
			
			return "user/userForm";			
		}else {
			model.addAttribute("msg", error);
			return "error";
		}
	}
	
	@GetMapping("/user/{userId}/calls/{employeeUsername}")
	public String listCallsByEmployee(Model model, @PathVariable String userId, @PathVariable String employeeUsername) throws UserException {
	    String error = "";
	    User user = new User();

	    try {
	        Integer idInt = userService.idIntoInt(userId);
	        Optional<User> userOpt = userService.findUserById(idInt);
	        if (userOpt.orElse(null) == null) {
	            error = "Usuario no encontrado.";
	        } else {
	            user = userOpt.get();
	            model.addAttribute("user", user);

	            Employee employee = employeeService.getEmployeeByUsername(employeeUsername).orElse(null);
	            if (employee == null) {
	                error = "Empleado no encontrado.";
	            } else {
	                List<Call> calls = callService.findByUserAndEmployee(user, employee);
	                model.addAttribute("selectedEmployeeCalls", calls);
	                model.addAttribute("selectedEmployee", employee);
	                model.addAttribute("disabled", true);
	            }
	        }
	    } catch (NumberFormatException ue) {
	        error = ue.getMessage();
	    }

	    if (error.isEmpty()) {
	        List<Call> latestCalls = callService.getLatestCallsByUser(user);
	        model.addAttribute("h1", "Detalles del usuario");
	        model.addAttribute("calls", latestCalls);
	        model.addAttribute("showCalls", true);
	        model.addAttribute("activity", "show");
	        return "user/userForm";
	    } else {
	        model.addAttribute("msg", error);
	        return "error";
	    }
	}


	
	
	@GetMapping("/user/{id}/calls/add")
	public String addCall(Model model, @PathVariable String id) {
		User user = new User();
		try {
			user = userService.getUserValid(id);
			model.addAttribute("user", user);
			
			List<Call> calls = callService.getLatestCallsByUser(user);
            model.addAttribute("calls", calls);
            
            if(!((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains("USER")) {
    			List<Call> latestCalls = callService.getLatestCallsByUser(user);
    	        model.addAttribute("calls", latestCalls);
    	        model.addAttribute("h1", "Detalles del usuario");
    	        model.addAttribute("showCalls", true);
    	        model.addAttribute("activity", "show");
    	        model.addAttribute("disabled", true);
    	        model.addAttribute("errorCallMsg", "Se debe tener el rol 'USER' para registrar llamadas");
    	        return "user/userForm";
    		}

		}catch(UserException ue) {
			model.addAttribute("msg", ue.getMessage());
			ue.printStackTrace();
			return "error";
		}

		/* HTML content for UserForm*/
		this.callService.getHtmlContentForUserForm(model, user);
		/* HTML content for callList*/
		
		/* HTML content for callForm*/
			//Crear nuevo objeto
		Call newCall = new Call();
		model.addAttribute("call", newCall);
		
		model.addAttribute("callAction", "/user/"+user.getId()+"/calls/added");
		model.addAttribute("showCalls", true);
		model.addAttribute("addCall", true);
		model.addAttribute("callActivity", "show");
		model.addAttribute("callDisabled", false);
			//Traer opciones de calltype
		List<CallType> callTypes = callTypeService.getAllCallTypes();
		model.addAttribute("callTypes", callTypes);

		
		return "user/userForm";
	}
	
	//Para hacer las pruebas con un registro existente:  http://localhost:8080/user/1/calls/edit/admin/1/2025-01-26
	@GetMapping("/user/{id}/calls/edit/{emplUsername}/{callTypeId}/{date}") //Aquí tendría que pasarle por parámetro todos los datos de la llamda que sirven para identificarlo, y a partir de ahí los recojo, valido y recupero la llamada
	public String editCall(Model model, 
							@PathVariable("id") String userId, 
							@PathVariable String emplUsername, 
							@PathVariable String callTypeId, 
							@PathVariable String date)
							{
//							@RequestParam String emplUsername, 
//							@RequestParam String callTypeId,
//							@RequestParam String date) {
		
		boolean validInformation =false;
		
		//Validamos y recuperamos al usuario
		User user = new User();
		try {
			user = userService.getUserValid(userId);
			model.addAttribute("user", user);
			
			if(!((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains("USER")) {
    			List<Call> latestCalls = callService.getLatestCallsByUser(user);
    	        model.addAttribute("calls", latestCalls);
    	        model.addAttribute("h1", "Detalles del usuario");
    	        model.addAttribute("showCalls", true);
    	        model.addAttribute("activity", "show");
    	        model.addAttribute("disabled", true);
    	        model.addAttribute("errorCallMsg", "Se debe tener el rol 'USER' para editar llamadas");
    	        return "user/userForm";
    		}
			
		}catch(UserException ue) {
			model.addAttribute("msg", ue.getMessage());
			ue.printStackTrace();
			return "error";
		}
		
		//No validamos al empleado porque se asumirá que está validado al haber iniciado sesión
		try {
			Integer userIdInt = this.userService.idIntoInt(userId);
			Integer callTypeIdInt = this.userService.idIntoInt(callTypeId);
			LocalDate callDate = LocalDate.parse(date);
			
			Call callToEdit = this.callService.getHigherOrderCall(userIdInt, emplUsername, callTypeIdInt, callDate);
			
			model.addAttribute("call", callToEdit);
			
			validInformation=true;
			
			if(!((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername().equals(callToEdit.getEmployee().getUsername())){
				model.addAttribute("errorCallMsg", "Sólo " + callToEdit.getEmployee().getUsername() + " puede editar esta llamada");
				validInformation=false;
			}
			
		}catch(NumberFormatException nfe) {//Si algún id no es numérico
			model.addAttribute("msg", nfe.getMessage());
			nfe.printStackTrace();
			return "error";
		}catch(CallException ce) {//Si la llamada no se encuentra. Volverá a mandar a la pantalla de visualización de historial, sin formulario
			model.addAttribute("errorCallMsg", ce.getMessage());
			ce.printStackTrace();
		}catch(DateTimeParseException dtpe) {
			model.addAttribute("msg", "Error en el formato de la fecha");
			dtpe.printStackTrace();
			return "error";
		}
		
		
		/* HTML content for UserForm*/
		this.callService.getHtmlContentForUserForm(model, user);
		/* HTML content for callList*/
		model.addAttribute("showCalls", true);
		
		/* HTML content for callForm*/
		if(validInformation) {
			model.addAttribute("editCall", true);
			model.addAttribute("callDisabled", false);
			model.addAttribute("callAction", "/user/"+user.getId()+"/calls/edited");
		}
		List<Call> calls = callService.getLatestCallsByUser(user);
        model.addAttribute("calls", calls);
        
		return "user/userForm";
	}
	
	
	
	/* ---------------- POST ---------------------*/
	
	@PostMapping("/user/{id}/calls/added")
	public String addCall(Model model, @Validated @ModelAttribute("call") Call newCall, BindingResult bindingResult, @PathVariable String id ) {
		
		/*---- User content -----*/
		User user = new User();
		try {
			user = userService.getUserValid(id);
			model.addAttribute("user", user);
		}catch(UserException ue) {
			model.addAttribute("msg", ue.getMessage());
			return "error";
		}
		/* HTML content for UserForm*/
		this.callService.getHtmlContentForUserForm(model, user);
		
		
		/*---- Call content -----*/
		if(!bindingResult.hasErrors()) {
			//Añadimos los datos que faltan
			newCall.setDate(LocalDate.now());
			newCall.setUser(user);
			newCall.setOrder(1);
			newCall.setEmployee(this.employeeService.getEmployeeByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null));
			
// ------------- CAMBIAR ESTA ASIGNACIÓN POR LA DEL USUARIO QUE HAYA INICIADO SESIÓN. Aquí creamos el usuario admin si no está creado previamente, para que nunca falle.
			//Validamos si el usuario admin ya está creado y si no lo creamos, para que no dé fallo. Todas las llamadas se asignarán a admin temporalmente
//			if(!this.employeeService.isAlreadyRegistered("admin")) {
//				Employee admin = new Employee();
//				admin.setUsername("admin");
//				admin.setName("admin");
//				admin.setLastName("admin");
//				admin.setPassword("admincillo");
//				admin.setRoles("ADMIN");
//				admin.setEmail("admin@admin.es");
//				this.employeeService.saveEmployee(admin);
//				newCall.setEmployee(admin);
//			}else {
//				newCall.setEmployee(this.employeeService.getEmployeeByUsername("admin").orElse(null));
//			}
			
//----------------------------------------------------------------------------------------
			
			//1. Validar la llamada : que no esté ya registrada 
			if(this.callService.callAlreadyExists(newCall)) {
				model.addAttribute("errorCallMsg", "La llamada ya está registrada. Edite la llamada para añadir información nueva.");
			} //	 Si no tiene el rol USER, no puede añadir llamada 
			else if(!((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains("USER")) {
				model.addAttribute("errorCallMsg", "Se debe tener el rol 'USER' para registrar llamadas");
			}
			else {// Guardamos la llamada
				try {
					this.callService.savecall(newCall);					
					model.addAttribute("successCallMsg", "Llamada registrada con éxito.");				
				}catch(CallException ce) {
					model.addAttribute("errorCallMsg", ce.getMessage());
				}
			}
			
		}else { // Si hay fallos
			model.addAttribute("callAction", "/user/"+user.getId()+"/calls/added");
			model.addAttribute("addCall", true);
			model.addAttribute("callDisabled", false);
			List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);

		}
		/* HTML content for callForm*/
		model.addAttribute("call", newCall);
		model.addAttribute("showCalls", true);
		
		List<Call> calls = callService.getLatestCallsByUser(user);
        model.addAttribute("calls", calls);
		
		return "user/userForm";
	}
	
	@PostMapping("/user/{id}/calls/edited")
	public String editCall(Model model, @Validated @ModelAttribute("call") Call editedCall, BindingResult bindingResult, @PathVariable String id ) {
		
		/*---- User content -----*/
		User user = new User();
		try {
			user = userService.getUserValid(id);
			model.addAttribute("user", user);
		}catch(UserException ue) {
			model.addAttribute("msg", ue.getMessage());
			return "error";
		}
		/* HTML content for UserForm*/
		this.callService.getHtmlContentForUserForm(model, user);
		
		/*---- Call content -----*/
		
		if(!bindingResult.hasErrors()) {
			//1. Validar la llamada : que esté ya registrada 
			
			if(!this.callService.callAlreadyExists(editedCall)) {
				model.addAttribute("errorCallMsg", "La llamada no está registrada. Registre una nueva llamada.");
			}//	 Si no tiene el rol USER, no puede editar llamada 
			else if(!((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains("USER")) {
				model.addAttribute("errorCallMsg", "Se debe tener el rol 'USER' para editar llamadas");
			} // Si un empleado intenta editar la llamada de otro, no puede editar llamada 
			else if(!((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername().equals(editedCall.getEmployee().getUsername())) {
				model.addAttribute("errorCallMsg", "Sólo " + editedCall.getEmployee().getUsername() + " puede editar esta llamada");
			} else {// Guardamos la llamada
				this.callService.editCall(model, editedCall);					
			}	
		}else {	
			model.addAttribute("editCall", true);
			model.addAttribute("callDisabled", false);
			model.addAttribute("callAction", "/user/"+user.getId()+"/calls/edited");
		}
		model.addAttribute("showCalls", true);
		List<Call> calls = callService.getLatestCallsByUser(user);
        model.addAttribute("calls", calls);
		
		return "user/userForm";
	}
}
