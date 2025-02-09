package com.telejaca.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.telejaca.model.Locality;
import com.telejaca.model.Phone;
import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.service.LocalityService;
import com.telejaca.service.PhoneService;
import com.telejaca.service.UserService;

@Controller
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	LocalityService localityService;
	@Autowired
	PhoneService phoneService;
	
	@GetMapping("/users")
	public String listUsers(
	    @RequestParam Optional<String> page, 
	    @RequestParam Optional<String> size, 
	    @RequestParam(required = false, defaultValue = "") String searchValue, 
	    Model model) {
	    
	    int currentPage;
	    int pageSize;

	    // Validación del número de página
	    try {
	        currentPage = Integer.parseInt(page.orElse("1"));
	    } catch (NumberFormatException e) {
	        currentPage = 1;
	    }

	    // Validación del tamaño de página
	    try {
	        pageSize = Integer.parseInt(size.orElse("10"));
	        if (pageSize < 1 || pageSize > 100) {
	            pageSize = 10;
	        }
	    } catch (NumberFormatException e) {
	        pageSize = 10;
	    }

	    // Obtener usuarios filtrados o todos los usuarios
	    Page<User> userPage;
	    if (searchValue.isEmpty()) {
	        userPage = userService.getAllActiveUsers(Optional.of(currentPage - 1), Optional.of(pageSize));
	    } else {
	        userPage = userService.searchActiveUser(searchValue, Optional.of(currentPage - 1), Optional.of(pageSize));
	    }

	    if (currentPage > userPage.getTotalPages()) {
	        currentPage = 1;
	        userPage = userService.searchActiveUser(searchValue, Optional.of(currentPage - 1), Optional.of(pageSize));
	    }

	    model.addAttribute("paginatedUsers", userPage.getContent());
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalPages", userPage.getTotalPages());
	    model.addAttribute("hasNext", userPage.hasNext());
	    model.addAttribute("hasPrevious", userPage.hasPrevious());
	    model.addAttribute("searchValue", searchValue);

	    return "user/userList";
	}

	@GetMapping("/call/callFirstStage")
	public String listSearchUsersToCall(
	    @RequestParam(defaultValue = "id") String sortField,
	    @RequestParam(defaultValue = "asc") String sortDirection,
	    @RequestParam Optional<String> page, 
	    @RequestParam Optional<String> size, 
	    @RequestParam(required = false, defaultValue = "") String searchValue, 
	    Model model) {

	    int currentPage;
	    int pageSize;

	    // Validación del número de página
	    try {
	        currentPage = Integer.parseInt(page.orElse("1"));
	    } catch (NumberFormatException e) {
	        currentPage = 1;
	    }

	    // Validación del tamaño de página
	    try {
	        pageSize = Integer.parseInt(size.orElse("10"));
	        if (pageSize < 1 || pageSize > 100) {
	            pageSize = 10;
	        }
	    } catch (NumberFormatException e) {
	        pageSize = 10;
	    }

	    // Obtener usuarios filtrados y ordenados
	    Page<User> userPage = userService.searchActiveUser(
	        searchValue, 
	        Optional.of(currentPage - 1), 
	        Optional.of(pageSize), 
	        sortField, 
	        sortDirection
	    );

	    if (currentPage > userPage.getTotalPages() && userPage.getTotalPages() > 0) {
	        currentPage = 1;
	        userPage = userService.searchActiveUser(
	            searchValue, 
	            Optional.of(currentPage - 1), 
	            Optional.of(pageSize), 
	            sortField, 
	            sortDirection
	        );
	    }

	    if (userPage.getTotalElements() == 0) {
	        model.addAttribute("errorMsg", "No se encontraron usuarios con ese criterio de búsqueda.");
	    }

	    model.addAttribute("paginatedUsers", userPage.getContent());
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalPages", userPage.getTotalPages());
	    model.addAttribute("hasNext", userPage.hasNext());
	    model.addAttribute("hasPrevious", userPage.hasPrevious());
	    model.addAttribute("searchValue", searchValue);
	    model.addAttribute("sortField", sortField);
	    model.addAttribute("sortDirection", sortDirection);

	    return "call/callFirstStage";
	}
	
	@GetMapping("/users/userSearch")
	public String listSearchUsers(Model model, @RequestParam(required=false, defaultValue="") String searchValue, @RequestParam Optional<String> page, @RequestParam Optional<String> size) {
		int currentPage;
	    int pageSize;

	    // Validación del número de página
	    try {
	        currentPage = Integer.parseInt(page.orElse("1"));
	    } catch (NumberFormatException e) {
	        currentPage = 1;
	    }

	    // Validación del tamaño de página
	    try {
	        pageSize = Integer.parseInt(size.orElse("10"));
	        if (pageSize < 1 || pageSize > 100) {
	            pageSize = 10;
	        }
	    } catch (NumberFormatException e) {
	        pageSize = 10;
	    }
	    
	 // Obtener usuarios filtrados
	    Page<User> userPage = userService.searchActiveUser(searchValue, Optional.of(currentPage - 1), Optional.of(pageSize));

		
	    if (currentPage > userPage.getTotalPages()) {
	        currentPage = 1;
	        userPage = userService.searchActiveUser(searchValue, Optional.of(currentPage - 1), Optional.of(pageSize));
	    }
	    
	    if (userPage.getTotalElements() == 0) {
	        model.addAttribute("errorMsg", "No se encontraron usuarios con ese criterio de búsqueda.");
	    }

	    model.addAttribute("paginatedUsers", userPage.getContent());
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalPages", userPage.getTotalPages());
	    model.addAttribute("hasNext", userPage.hasNext());
	    model.addAttribute("hasPrevious", userPage.hasPrevious());
	    model.addAttribute("searchValue", searchValue);
	    
		return "user/userList";
	}
	
	
	@GetMapping("/user/{id}")
	public String showUserDetails(Model model, @PathVariable String id) {
		
		User user = null;
		try {
			user = userService.getUserValid(id);
			model.addAttribute("user", user);
		}catch(UserException ue) {
			model.addAttribute("msg", ue.getMessage());
			return "error";
		}
		model.addAttribute("h1", "Detalles del usuario");
		model.addAttribute("disabled", true);
		model.addAttribute("action", "");
		model.addAttribute("activity", "show");
		String localityName = "";
		if(user.getLocality()!=null) {
			localityName = user.getLocality().getName()==null ? "Sin registro" : user.getLocality().getName();				
		}
		model.addAttribute("localityName", localityName);
			
		return "user/userForm";			

	}
	
	@GetMapping("/user/add")
	public String addUser(Model model) {		
		/* User information */
		User user = new User();
	    user.getPhoneList().add(new Phone()); // Agrega un teléfono por defecto para capturar datos
	    model.addAttribute("user", user);
	    
		model.addAttribute("careRelation", new CaregiverRelation());
		/* Users list */ /* Plantear traer sólo el id y el nombre del usuario */
		try {
			List<User> users = userService.getAllActiveUsers();
			model.addAttribute("users", users);
		}catch(UserException ue) {
			model.addAttribute("noUsersMsg", ue.getMessage());
		}
		/* Other HTML content */
		model.addAttribute("action", "/users/add");
		model.addAttribute("h1", "Formulario de alta");
		model.addAttribute("disabled", false);
		model.addAttribute("activity", "add");
		List<Locality> localityList=localityService.getAllLocalitys();
		model.addAttribute("localityList", localityList);
		return "user/userForm";
	}
	
	@GetMapping("/user/edit/{id}")
	public String editUser(Model model, @PathVariable String id) {
		
		/* Validations */
		String error = "";
		boolean notFound = false;
		String template = "";
		
		if(id.isBlank()) {
			error = "Id de usuario vacía.";
		}else {
			try {
				Integer idInt = userService.idIntoInt(id);
				Optional<User> userOpt = userService.findUserById(idInt);

				if (userOpt.orElse(null)==null) {
				    error = "Usuario no encontrado.";
				    notFound = true;
				} else {
				    User user = userOpt.get();
				    model.addAttribute("user", user);
				    Integer userId = user.getId();
				    model.addAttribute("userId", userId);
				    
				    // Si no tiene teléfono, inicializo uno vacío
				    if (user.getPhoneList().isEmpty()) {
				    	user.getPhoneList().add(new Phone()); // Agregar el teléfono a la lista
				    }
				    
				}
			}catch (NoSuchElementException nse) {
				error = "Usuario no encontrado.";
			}catch(UserException ue) {
				error = ue.getMessage();
			}catch(NumberFormatException ue) {
				error = ue.getMessage();
			}
		}
		/* If there are errors, it will send to the error page. Otherwise, it will show the form with the user information.*/
		
		if(notFound) {
			model.addAttribute("errorMsg", error);
			template="call/callFirstStage";
			try {
				List<User> users = userService.getAllActiveUsers();
				model.addAttribute("users", users);
			}catch(UserException ue) {
				model.addAttribute("noUsersMsg", ue.getMessage());
			}
		}else if(error.isEmpty()) {
			/* HTML content */
			model.addAttribute("h1", "Editar información del usuario");
			model.addAttribute("action", "/user/edit");
			model.addAttribute("disabled", false);
			model.addAttribute("activity", "edit");
			List<Locality> localityList=localityService.getAllLocalitys();
			model.addAttribute("localityList", localityList);
			
			template= "user/userForm";			
		}else {
			model.addAttribute("msg", error);
			template= "error";
		}
		return template;
		
	}
	
	@GetMapping("/user/delete/{id}")
	public String deleteUser(Model model, @PathVariable String id) {
		User user = null;
		try {
			user = userService.getUserValid(id);
			model.addAttribute("user", user);
		}catch(UserException ue) {
			model.addAttribute("msg", ue.getMessage());
			return "error";
		}
		model.addAttribute("h1", "Formulario de baja");
		model.addAttribute("disabled", false);
		model.addAttribute("action", "/user/delete/userDeleted/");
		model.addAttribute("activity", "delete");
		String localityName = "";
		if(user.getLocality()!=null) {
			localityName = user.getLocality().getName()==null ? "Sin registro" : user.getLocality().getName();				
		}
		model.addAttribute("localityName", localityName);
			
		return "user/userForm";			

	}
	
	/* POST */

	@PostMapping("/users/add")
	public String addingUser(Model model, @Validated @ModelAttribute("user") User u, BindingResult bindingResult) {
		
		boolean added = false;
		String template = "user/userForm";
		
		/* Validaciones - (Pablo -> Omito la comprobación del usuario asignado al teléfono, se lo añado luego) */
		if(!bindingResult.hasErrors() || bindingResult.hasFieldErrors("phoneList[0].phoneUser")) {
			try {
				
				for (Phone phone : u.getPhoneList()) {
	                phone.setPhoneUser(u);
	            }
				
				Integer newUserId = userService.addUser(u);		
				model.addAttribute("userId", newUserId);
				added=true;
				
			}catch(UserException e) {
				model.addAttribute("error", "Error al dar de alta. Vuelva a intentarlo. Si el problema persiste, contacte con su administrador.");
				//Tiene esto sentido? el syso lo verá un desarrollador? entiendoq ue sí. 
				System.out.println("Error al añadir usuario: "+e.getMessage());
			}
		}
		
		/* HTML content  */
		if(added) {
			template = "user/userForm.html";
			
			model.addAttribute("miniNav", "");
			model.addAttribute("showPhones", false); 
			model.addAttribute("showCalls", false);
			model.addAttribute("showMedicationList", false);
			model.addAttribute("showRelations", false);
			
			model.addAttribute("user", u);
			model.addAttribute("h1", "Detalles del usuario");
			model.addAttribute("disabled", true);
			model.addAttribute("action", "");
			model.addAttribute("activity", "show");
			String localityName = u.getLocality().getName()==null ? "Sin registro" : u.getLocality().getName();
			model.addAttribute("localityName", localityName);
		}else {
			model.addAttribute("action", "/users/add");
			model.addAttribute("h1", "Formulario de alta");
			model.addAttribute("disabled", false);
			model.addAttribute("activity", "add");
			model.addAttribute("localityList", localityService.getAllLocalitys());
		}
		return template;
	}
	
	/* Aquí plantear no eliminar el usuario como tal, sino eliminar sus datos relevantes menos su nombre, añadirlo en la vista a una sección de "bajas", y abrir un formulario de bajas donde se tenga que indicar el motivo.  */
	@PostMapping("user/delete/userDeleted/")
	public String userDeleted(Model model, @Validated @ModelAttribute User userToDelete, BindingResult bindingResult) {
		
		boolean deleted = false;
		String template = "user/userForm";
		
		/* Si el campo de motivo de cancelación está vacío o es nulo */
		if(userToDelete.getCancellationCause().isBlank() || userToDelete.getCancellationCause().equals(null)) {
			model.addAttribute("errorMsg", "Debe indicar un motivo para la baja.");

		}else if (!bindingResult.hasErrors()) { /* Si NO hay ningún error:  */
			try {
				/* Borrado lógido del usuario */
				userService.unsubscribeUser(userToDelete);
				deleted = true;
			}catch(UserException ue) {
				model.addAttribute("errorMsg", "Error al dar de baja a la persona usuaria. Vuelva a intentarlo. Si el problema persiste, contacte con su administrador.");
				System.out.println(ue.getMessage());
			}
		}
		/* HTML content  */
		if(deleted) {
			model.addAttribute("msg", "Usuario/a "+userToDelete.getName()+" "+userToDelete.getSurname()+" ha sido dado de baja con éxito.");
			template = "index.html";
			try {
				List<User> users = userService.getAllActiveUsers();
				model.addAttribute("users", users);
			}catch(UserException ue) {
				model.addAttribute("noUsersMsg", ue.getMessage());
			}
		}else {
			model.addAttribute("h1", "Formulario de baja");
			model.addAttribute("disabled", false);
			model.addAttribute("action", "/user/delete/userDeleted/");
			model.addAttribute("activity", "delete");
			model.addAttribute("userId", userToDelete.getId());
		}
		return template;
	}
	
	@PostMapping("/user/edit")
	public String userEdited(Model model, @Validated @ModelAttribute User userToEdit, BindingResult bindingResult) {
		
		boolean edited = false;
		

		if(userToEdit.getLocality() != null && userToEdit.getLocality().getId() == -1) {
			userToEdit.setLocality(null);
		}
	    /* Validaciones - (Pablo -> Omito la comprobación del usuario asignado al teléfono, se lo añado luego) */
		if(!bindingResult.hasErrors() || bindingResult.hasFieldErrors("phoneList[0].phoneUser")) 
		{

			try {
				if (userToEdit.getPhoneList().get(0).getPhoneUser() == null) {
					userToEdit.getPhoneList().get(0).setPhoneUser(userToEdit);
				}
				
				this.userService.editUser(userToEdit);
				edited = true;
			}catch(UserException e) {
				model.addAttribute("error", "Error al editar la información. Vuelva a intentarlo. Si el problema persiste, contacte con su administrador.");
				System.out.println(e.getMessage());
			}
		}
		
		model.addAttribute("localityList", localityService.getAllLocalitys());
		
		if(edited) {
			model.addAttribute("h1", "Información editada");
			model.addAttribute("disabled", true);
			model.addAttribute("action", "");
			model.addAttribute("activity", "edited");
			model.addAttribute("successMsg", "La información de "+userToEdit.getName()+" ha sido editada con éxito");
			
		}else {
			model.addAttribute("h1", "Editar información del usuario");
			model.addAttribute("disabled", false);
			
			model.addAttribute("action", "/user/edit");
			model.addAttribute("activity", "edit");
		}
		model.addAttribute("user", userToEdit);
		model.addAttribute("userId", userToEdit.getId());
		return "user/userForm";
	}
}
