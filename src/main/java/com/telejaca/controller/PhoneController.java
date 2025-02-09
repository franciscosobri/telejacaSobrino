package com.telejaca.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telejaca.model.Locality;
import com.telejaca.model.Phone;
import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.service.LocalityService;
import com.telejaca.service.PhoneService;
import com.telejaca.service.UserService;

@Controller
public class PhoneController {
	@Autowired
	PhoneService phoneService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LocalityService localityService;
	
	@GetMapping("/phones")
	public String listPhones(Model model) {
		List<Phone> phones = phoneService.getAllPhones();
		model.addAttribute("phones", phones);
		return "phones/phoneList";
	}
	
	
	@GetMapping("/phone/add")
	public String addPhone(Model model) {
		
		model.addAttribute("phone", new Phone());
		model.addAttribute("activity", "add");
		model.addAttribute("enable", true);

		model.addAttribute("h1", "Añadir teléfono");
		model.addAttribute("action", "/phone/add");
		
		model.addAttribute("users", userService.getAllUsers());

		return "phones/phoneTemplate";
	}
	

	@PostMapping("/phone/add")
	public String addPhonePost(Model model, @Validated @ModelAttribute Phone phone, BindingResult bindingResult) {
	    return processPhone(model, phone, bindingResult, "add");
	}
	
	
	@GetMapping("/phone/delete/{id}")
    public String deletePhone(Model model, @PathVariable String id) {
        Phone p = validateAndGetPhone(id, model);
        if (p == null) {
            return "phones/phoneList";
        }

        model.addAttribute("activity", "delete");
        model.addAttribute("enable", false);
        model.addAttribute("h1", "Eliminar teléfono");
        model.addAttribute("action", "/phone/delete");
        model.addAttribute("phone", p);
        
        model.addAttribute("users", userService.getAllUsers());

        return "phones/phoneTemplate";
    }
	
	@PostMapping("/phone/delete")
	public String deletePhonePost(Model model, @ModelAttribute Phone phone) {
	    return processPhone(model, phone, null, "delete");
	}
	
	
	@GetMapping("/phone/edit/{id}")
    public String editPhone(Model model, @PathVariable String id) {
        Phone p = validateAndGetPhone(id, model);
        if (p == null) {
            return "phones/phoneList";
        }

        model.addAttribute("activity", "edit");
        model.addAttribute("h1", "Editar teléfono");
        model.addAttribute("action", "/phone/edit");
        model.addAttribute("enable", true);
        model.addAttribute("phone", p);
        
        model.addAttribute("users", userService.getAllUsers());

        return "phones/phoneTemplate";
    }
	
	@PostMapping("/phone/edit")
	public String editPhonePost(Model model, @Validated @ModelAttribute Phone phone, BindingResult bindingResult) {
	    return processPhone(model, phone, bindingResult, "edit");
	}
	
	
	@GetMapping("/phone/show/{id}")
    public String showPhone(Model model, @PathVariable String id) {
        Phone p = validateAndGetPhone(id, model);
        if (p == null) {
            return "phones/phoneList";
        }

        model.addAttribute("activity", "show");
        model.addAttribute("h1", "Ver teléfono");
        model.addAttribute("action", "/phone/show");
        model.addAttribute("enable", false);
        model.addAttribute("phone", p);
        
        model.addAttribute("users", userService.getAllUsers());

        return "phones/phoneTemplate";
    }
	
	private Phone validateAndGetPhone(String id, Model model) {
        model.addAttribute("msgClass", "alert-danger");

        if (id == null || id.isBlank()) {
            model.addAttribute("msg", "Id vacía.");
            return null;
        }

        if (!phoneService.isInteger(id)) {
            model.addAttribute("msg", "Id inválida.");
            return null;
        }

        Integer idInt = Integer.parseInt(id);
        Phone p = phoneService.getPhoneById(idInt);
        if (p == null) {
            model.addAttribute("msg", "Teléfono no encontrado.");
            return null;
        }

        return p;
    }
	
	
	private String processPhone(Model model, Phone phone, BindingResult bindingResult, String action) {
	    model.addAttribute("msgClass", "alert-danger");
	    model.addAttribute("activity", action);
	    model.addAttribute("phone", phone);
	    model.addAttribute("h1", getActionTitle(action));
	    model.addAttribute("action", "/phone/" + action);
	    
	    model.addAttribute("users", userService.getAllUsers());

	    // Validación para add y edit
	    if ("add".equals(action) || "edit".equals(action)) {
	        if (bindingResult != null && bindingResult.hasErrors()) {
	            model.addAttribute("msg", "Por favor, corrige los errores en el formulario.");
	            return "phones/phoneTemplate";
	        }
	        
	        // Verificar duplicados
	        List<Phone> userPhones = phoneService.getPhonesByUser(phone.getPhoneUser());
	        boolean phoneExists = userPhones.stream().anyMatch(p -> 
	            p.getNumber().equals(phone.getNumber()) && 
	            (!"edit".equals(action) || !p.getId().equals(phone.getId()))
	        );

	        if (phoneExists) {
	            model.addAttribute("msg", "El usuario ya tiene ese número de teléfono registrado.");
	            return "phones/phoneTemplate";
	        }
	    }

	    try {
	        switch (action) {
	            case "add":
	                Phone newPhone = phoneService.addPhone(phone);
	                model.addAttribute("msg", "El teléfono " + newPhone.getNumber() + " ha sido añadido con éxito.");
	                model.addAttribute("msgClass", "alert-success");
	                break;
	            case "edit":
	                phoneService.editPhone(phone);
	                model.addAttribute("msg", "Teléfono editado con éxito.");
	                model.addAttribute("msgClass", "alert-success");
	                
	                model.addAttribute("activity", "edited");
	        		model.addAttribute("enable", false);
	                break;
	            case "delete":
	                Phone phoneToDelete = phoneService.getPhoneById(phone.getId());
	                if (phoneToDelete == null) {
	                    model.addAttribute("msg", "Error: No se pudo eliminar el teléfono. No existe en la base de datos.");
	                    return "phones/phoneList";
	                }
	                String number = phoneToDelete.getNumber();
	                phoneService.deletePhone(phoneToDelete);
	                model.addAttribute("msg", "Teléfono " + number + " borrado con éxito.");
	                model.addAttribute("msgClass", "alert-success");
	                break;
	        }
	    } catch (IllegalArgumentException e1) {
	        model.addAttribute("msg", "Id inapropiado.");
	    } catch (OptimisticLockingFailureException e2) {
	        model.addAttribute("msg", "El teléfono no ha podido ser procesado.");
	    }

	    List<Phone> phones = phoneService.getAllPhones();
	    model.addAttribute("phones", phones);
	    
	    return !"edit".equals(action) ? "phones/phoneList" : "phones/phoneTemplate";
	}

	private String getActionTitle(String action) {
	    switch (action) {
	        case "add":
	            return "Añadir teléfono";
	        case "edit":
	            return "Editar teléfono";
	        case "delete":
	            return "Eliminar teléfono";
	        default:
	            return "Teléfono";
	    }
	}
	
	
	// Métodos para los teléfonos asociados a un usuario en particular 
	// (Vista detalles del usuario)
	
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
	    model.addAttribute("userId", user.getId());
	    model.addAttribute("activity", activity);
	    model.addAttribute("enable", enable);
	    model.addAttribute("h1", h1);
	    model.addAttribute("action", action);

		String localityName = user.getLocality().getName()==null ? "Sin registro" : user.getLocality().getName();
		model.addAttribute("localityName", localityName);

	    List<Phone> phones = phoneService.getPhonesByUser(user);
	    model.addAttribute("phones", phones);
	    model.addAttribute("showPhones", true);
	}
	
	// Manejar errores
	private String handleError(Model model, String errorMessage, String viewName) {
	    model.addAttribute("msg", errorMessage);
	    return viewName;
	}
	
	// Mostrar lista de teléfonos asociados al usuario
	@GetMapping("/user/{id}/phones")
	public String listPhones(Model model, @PathVariable String id) throws UserException {
	    User user = validateAndGetUser(id, model);
	    if (user == null) {
	        return handleError(model, "Error al obtener el usuario.", "error");
	    }

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    model.addAttribute("disabled", true);
	    return "user/userForm";
	}
	
	// Show more
	@GetMapping("/user/phone/show/{id}")
	public String showUserPhone(Model model, @PathVariable String id) {
	    // Validar y obtener el teléfono
	    Phone phone = validateAndGetPhone(id, model);
	    if (phone == null) {
	        return handleError(model, "Error al obtener el teléfono.", "error");
	    }

	    // Obtener el usuario asociado al teléfono
	    User user = phone.getPhoneUser();

	    // Configurar el modelo común para la vista
	    setupUserModel(model, user, "show", false, "Ver teléfono", "/phone/show");
	    model.addAttribute("phone", phone);

	    return "phones/userPhoneTemplate";
	}
	
	// GET add
	@GetMapping("/user/phone/add/{id}")
	public String addUserPhone(Model model, @PathVariable String id) throws UserException {
	    User user = validateAndGetUser(id, model);
	    if (user == null) {
	        return handleError(model, "Error al obtener el usuario.", "error");
	    }

	    model.addAttribute("phone", new Phone());
	    setupUserModel(model, user, "add", true, "Añadir teléfono", "/user/phone/add");
	    return "phones/userPhoneTemplate";
	}
	
	// POST add
	@PostMapping("/user/phone/add")
	public String addUserPhonePost(Model model, @Validated @ModelAttribute Phone phone, BindingResult bindingResult, @RequestParam("phoneUserId") Integer phoneUserId) throws UserException {
	    User user = validateAndGetUser(phoneUserId.toString(), model);
	    if (user == null) {
	        return handleError(model, "El usuario asociado al teléfono no es válido.", "error");
	    }

	    phone.setPhoneUser(user);
	    model.addAttribute("user", user);

	    if (bindingResult != null && bindingResult.hasErrors()) {
	        setupUserModel(model, user, "add", true, "Añadir teléfono", "/user/phone/add");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "Por favor, corrige los errores en el formulario.");
	        return "phones/userPhoneTemplate";
	    }

	    if (phoneService.getPhonesByUser(user).stream().anyMatch(p -> p.getNumber().equals(phone.getNumber()))) {
	        setupUserModel(model, user, "add", true, "Añadir teléfono", "/user/phone/add");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "El usuario ya tiene ese número de teléfono registrado.");
	        return "phones/userPhoneTemplate";
	    }

	    Phone newPhone = phoneService.addPhone(phone);
	    model.addAttribute("msgClass", "alert-success");
	    model.addAttribute("msg", "El teléfono " + newPhone.getNumber() + " ha sido añadido con éxito.");

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    return "user/userForm";
	}
	
	// GET delete
	@GetMapping("/user/phone/delete/{id}")
	public String deleteUserPhone(Model model, @PathVariable String id) {
	    Phone phone = validateAndGetPhone(id, model);
	    if (phone == null) {
	        return handleError(model, "Error al obtener el teléfono.", "error");
	    }

	    User user = phone.getPhoneUser();
	    setupUserModel(model, user, "delete", false, "Eliminar teléfono", "/user/phone/delete");
	    model.addAttribute("phone", phone);
	    return "phones/userPhoneTemplate";
	}
	
	// POST delete
	@PostMapping("/user/phone/delete")
	public String deleteUserPhonePost(Model model, @ModelAttribute Phone phone, @RequestParam("phoneUserId") Integer phoneUserId) throws UserException {
	    User user = validateAndGetUser(phoneUserId.toString(), model);
	    if (user == null) {
	        return handleError(model, "El usuario asociado al teléfono no es válido.", "error");
	    }

	    Phone phoneToDelete = phoneService.getPhoneById(phone.getId());
	    if (phoneToDelete == null) {
	        return handleError(model, "Error: No se pudo eliminar el teléfono. No existe en la base de datos.", "error");
	    }

	    phoneService.deletePhone(phoneToDelete);
	    model.addAttribute("msgClass", "alert-success");
	    model.addAttribute("msg", "Teléfono " + phoneToDelete.getNumber() + " borrado con éxito.");

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    return "user/userForm";
	}
	
	// GET edit
	@GetMapping("/user/phone/edit/{id}")
	public String editUserPhone(Model model, @PathVariable String id) {
	    Phone phone = validateAndGetPhone(id, model);
	    if (phone == null) {
	        return handleError(model, "Error al obtener el teléfono.", "error");
	    }

	    User user = phone.getPhoneUser();
	    setupUserModel(model, user, "edit", true, "Editar teléfono", "/user/phone/edit");
	    model.addAttribute("phone", phone);
	    return "phones/userPhoneTemplate";
	}
	
	// POST edit
	@PostMapping("/user/phone/edit")
	public String editUserPhonePost(Model model, @Validated @ModelAttribute Phone phone, BindingResult bindingResult, @RequestParam("phoneUserId") Integer phoneUserId) throws UserException {
	    User user = validateAndGetUser(phoneUserId.toString(), model);
	    if (user == null) {
	        return handleError(model, "El usuario asociado al teléfono no es válido.", "error");
	    }

	    phone.setPhoneUser(user);
	    model.addAttribute("user", user);

	    if (bindingResult != null && bindingResult.hasErrors()) {
	        setupUserModel(model, user, "edit", true, "Editar teléfono", "/user/phone/edit");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "Por favor, corrige los errores en el formulario.");
	        return "phones/userPhoneTemplate";
	    }

	    if (phoneService.getPhonesByUser(user).stream().anyMatch(p -> p.getNumber().equals(phone.getNumber()) && !p.getId().equals(phone.getId()))) {
	        setupUserModel(model, user, "edit", true, "Editar teléfono", "/user/phone/edit");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "El usuario ya tiene ese número de teléfono registrado.");
	        return "phones/userPhoneTemplate";
	    }

	    phoneService.editPhone(phone);
	    model.addAttribute("msgClass", "alert-success");
	    model.addAttribute("msg", "Teléfono " + phone.getNumber() + " editado con éxito.");

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    return "user/userForm";
	}
}
