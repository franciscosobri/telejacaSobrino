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

import com.telejaca.model.Medication;
import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.service.LocalityService;
import com.telejaca.service.MedicationService;
import com.telejaca.service.UserService;

@Controller
public class MedicationController {
	@Autowired
	MedicationService medicationService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LocalityService localityService;
	
	@GetMapping("/medicationList")
	public String listMedications(Model model) {
		List<Medication> medications = medicationService.getAllMedications();
		//No muestro los que estén dados de baja
        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
		model.addAttribute("medicationList", medications);
		return "medication/medicationList";
	}
	
	@GetMapping("/medication/show/{id}")
	public String showMedication(Model model, @PathVariable String id) {
	    
	    if (!id.matches("[0-9]+")) {
	        model.addAttribute("msg", "ID inválido.");
	        List<Medication> medications = medicationService.getAllMedications();
	      //No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	        model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    }

	    Optional<Medication> optionalMedication = medicationService.getMedicationById(Integer.parseInt(id));
	    
	    if (optionalMedication.isEmpty()) {
	        model.addAttribute("msg", "Medicamento no encontrado.");
	        List<Medication> medications = medicationService.getAllMedications();
	      //No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	        model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    }
	    
	    try {
			model.addAttribute("userList", userService.getAllActiveUsers());
		} catch (UserException e) {
			e.printStackTrace();
		}

	    Medication medication = optionalMedication.get();
	    
	    model.addAttribute("medication", medication);
	    model.addAttribute("activity", "show");
	    model.addAttribute("enable", false);
	    model.addAttribute("h1", "Ver medicamento");
	    model.addAttribute("action", "/medication/show/" + id);

	    return "medication/medicationForm";
	}
	
	@GetMapping("/medication/add")
	public String toAdd(Model model) {
		
		model.addAttribute("medication", new Medication());
		try {
			model.addAttribute("userList", userService.getAllActiveUsers());
		} catch (UserException e) {
			e.printStackTrace();
		}
		model.addAttribute("activity", "add");
		model.addAttribute("enable", true);
		model.addAttribute("h1", "Añadir medicamento");
		model.addAttribute("action", "/medicationForm/add");

		return "medication/medicationForm";
	}
	
	@PostMapping("/medicationForm/add")
	public String addMedication(Model model, @Validated @ModelAttribute Medication medication, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("activity", "add");
	        return "medication/medicationForm";
	    }

	    if (medicationService.medicationExists(medication)) {
	    	String message = "El medicamento '" + medication.getDescription() + "' ya existe para el usuario '" + medication.getUser().getName() + "'.";
	        model.addAttribute("msg", message);
	        model.addAttribute("activity", "add");
	        model.addAttribute("h1", "Añadir medicamento");
	        model.addAttribute("enable", true);
	        return "medication/medicationForm";
	    }

	    try {
	        Medication newMedication = medicationService.addMedication(medication);
	        if (medication == null) {
	            throw new IllegalStateException("Error inesperado al añadir el medicamento.");
	        }
	        String message = "El medicamento '" + newMedication.getDescription() + "' con ID " + newMedication.getId() + " ha sido añadido.";
	        model.addAttribute("msg", message);
	        List<Medication> medications = medicationService.getAllMedications();
			//No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
			model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    } catch (Exception e) {
	        model.addAttribute("msg", "Ocurrió un error al añadir el medicamento: " + e.getMessage());
	        model.addAttribute("activity", "add");
	        return "medication/medicationForm";
	    }
	}

	@GetMapping("/medication/delete/{id}")
	public String toDelete(Model model, @PathVariable String id) {
		
		if (!id.matches("[0-9]+")) {
	        model.addAttribute("msg", "ID inválido.");
	        List<Medication> medications = medicationService.getAllMedications();
	        //No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	        model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    }

	    Optional<Medication> optionalMedication = medicationService.getMedicationById(Integer.parseInt(id));
	    
	    if (optionalMedication.isEmpty()) {
	        model.addAttribute("msg", "Medicamento no encontrado.");
	        List<Medication> medications = medicationService.getAllMedications();
	        //No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	        model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    }
	    
	    try {
			model.addAttribute("userList", userService.getAllActiveUsers());
		} catch (UserException e) {
			e.printStackTrace();
		}
	    
	    Medication medication = optionalMedication.get();

	    model.addAttribute("medication", medication);
	    model.addAttribute("activity", "delete");
	    model.addAttribute("enable", false);
	    model.addAttribute("h1", "Eliminar medicamento");
	    model.addAttribute("action", "/medication/delete/" + id);

	    return "medication/medicationForm";
	}
	
	@PostMapping("/medication/delete/{id}")
	public String deleteMedication(Model model, @PathVariable Integer id) {
	    try {
	        Optional<Medication> medicationToDelete = medicationService.getMedicationById(id);
	        if (medicationToDelete.isPresent()) {
	        	medicationService.deleteMedication(medicationToDelete.get());
	            model.addAttribute("msg", "Medicamento '" + medicationToDelete.get().getDescription() + "' borrado con éxito.");
	        } else {
	            model.addAttribute("msg", "Medicamento no encontrado.");
	        }
	    } catch (IllegalArgumentException e1) {
	        model.addAttribute("msg", "Id inapropiado.");
	    } catch (OptimisticLockingFailureException e2) {
	        model.addAttribute("msg", "El medicamento no ha podido ser eliminado.");
	    } catch (Exception e) {
	        model.addAttribute("msg", "Ocurrió un error inesperado.");
	    }

	    List<Medication> medications = medicationService.getAllMedications();
	    //No muestro los que estén dados de baja
        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	    model.addAttribute("medicationList", medications);
	    return "medication/medicationList";
	}
	
	@GetMapping("/medication/edit/{id}")
	public String toEdit(Model model, @PathVariable String id) {
	    
		if (!id.matches("[0-9]+")) {
	        model.addAttribute("msg", "ID inválido.");
	        List<Medication> medications = medicationService.getAllMedications();
	      //No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	        model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    }

	    Optional<Medication> optionalMedication = medicationService.getMedicationById(Integer.parseInt(id));
	    
	    if (optionalMedication.isEmpty()) {
	        model.addAttribute("msg", "Medicamento no encontrado.");
	        List<Medication> medications = medicationService.getAllMedications();
	      //No muestro los que estén dados de baja
	        medications.removeIf(m -> m.getUser() != null && m.getUser().getCancellationDate() != null);
	        model.addAttribute("medicationList", medications);
	        return "medication/medicationList";
	    }
	    
	    try {
			model.addAttribute("userList", userService.getAllActiveUsers());
		} catch (UserException e) {
			e.printStackTrace();
		}

	    Medication medication = optionalMedication.get();
	    
	    model.addAttribute("medication", medication);
	    model.addAttribute("activity", "edit");
	    model.addAttribute("h1", "Editar medicamento");
	    model.addAttribute("action", "/medication/edit/" + id);
	    model.addAttribute("enable", true);

	    return "medication/medicationForm";
	}
	
	@PostMapping("/medication/edit/{id}")
	public String editMedication(Model model, @Validated @ModelAttribute Medication medication, @PathVariable String id, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	        model.addAttribute("activity", "edit");
	        return "medication/medicationForm";
	    }
		
		if (medicationService.medicationExists(medication)) {
	        String message = "El medicamento '" + medication.getDescription() + "' ya existe para el usuario con '" + medication.getUser().getName() + "'.";
	        model.addAttribute("msg", message);
	        model.addAttribute("activity", "edit");
	        model.addAttribute("h1", "Editar medicamento");
	        model.addAttribute("enable", true);
	        try {
				model.addAttribute("userList", userService.getAllActiveUsers());
			} catch (UserException e) {
				e.printStackTrace();
			}
	        return "medication/medicationForm";
	    }

	    Medication editedMedication = medicationService.editMedication(medication);
	    
	    String message = (editedMedication == null)
	        ? "Error al editar el medicamento."
	        : "El medicamento '" + editedMedication.getDescription() + "' con ID " + editedMedication.getId() + " ha sido editado.";
	    
	    model.addAttribute("msg", message);
	    model.addAttribute("activity", "show");
		model.addAttribute("enable", false);
		model.addAttribute("h1", "Ver medicamento");
		model.addAttribute("action", "/medication/show/" + editedMedication.getId());
		try {
			model.addAttribute("userList", userService.getAllActiveUsers());
		} catch (UserException e) {
			e.printStackTrace();
		}
		
		return "medication/medicationForm";
	}
	
	private Medication validateAndGetMedication(String id, Model model) {
        model.addAttribute("msgClass", "alert-danger");

        if (id == null || id.isBlank()) {
            model.addAttribute("msg", "Id vacía.");
            return null;
        }

        if (!medicationService.isInteger(id)) {
            model.addAttribute("msg", "Id inválida.");
            return null;
        }

        Integer idInt = Integer.parseInt(id);
        Optional<Medication> optionalMedication = medicationService.getMedicationById(idInt);
        
        Medication m = optionalMedication.get();
        
        if (m == null) {
            model.addAttribute("msg", "Medicamento no encontrado.");
            return null;
        }

        return m;
    }
	
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

	    List<Medication> medicationList = user.getMedicationList();
	    model.addAttribute("medicationList", medicationList);
	    model.addAttribute("showMedicationList", true);
	}
	
	// Manejar errores
	private String handleError(Model model, String errorMessage, String viewName) {
	    model.addAttribute("msg", errorMessage);
	    return viewName;
	}
	
	// Mostrar lista de medicamentos asociados a un usuario
	@GetMapping("/user/{id}/medicationList")
	public String userMedicationList(Model model, @PathVariable String id) throws UserException {
		User user = validateAndGetUser(id, model);
	    if (user == null) {
	        return handleError(model, "Error al obtener el usuario.", "error");
	    }

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    model.addAttribute("disabled", true);
	    return "user/userForm";
	}
	
	// Ver más
	@GetMapping("/user/medication/show/{id}")
	public String showUserMedication(Model model, @PathVariable String id) {
		
	    // Validar y obtener el medicamento
	    Medication medication = validateAndGetMedication(id, model);
	    
	    if (medication == null) {
	        return handleError(model, "Error al obtener el teléfono.", "error");
	    }

	    // Obtener el usuario asociado al medicamento
	    User user = medication.getUser();

	    // Configurar el modelo común para la vista
	    setupUserModel(model, user, "show", false, "Ver medicamento", "/medication/show");
	    model.addAttribute("medication", medication);

	    return "medication/userMedicationTemplate";
	}
	
	// GET add
	@GetMapping("/user/medication/add/{id}")
	public String addUserMedication(Model model, @PathVariable String id) throws UserException {
	    User user = validateAndGetUser(id, model);
	    if (user == null) {
	        return handleError(model, "Error al obtener el usuario.", "error");
	    }

	    model.addAttribute("medication", new Medication());
	    setupUserModel(model, user, "add", true, "Añadir medicamento", "/user/medication/add");
	    return "medication/userMedicationTemplate";
	}
		
	// POST add
	@PostMapping("/user/medication/add")
	public String addUserMedicationPost(Model model, @Validated @ModelAttribute Medication medication, BindingResult bindingResult, @RequestParam Integer medicationUserId) throws UserException {
	    User user = validateAndGetUser(medicationUserId.toString(), model);
	    if (user == null) {
	        return handleError(model, "El usuario asociado al medicamento no es válido.", "error");
	    }

	    medication.setUser(user);
	    model.addAttribute("user", user);

	    if (bindingResult != null && bindingResult.hasErrors()) {
	        setupUserModel(model, user, "add", true, "Añadir medicamento", "/user/medication/add");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "Por favor, corrige los errores en el formulario.");
	        return "medication/userMedicationTemplate";
	    }

	    if (medicationService.medicationExists(medication)) {
	        setupUserModel(model, user, "add", true, "Añadir medicamento", "/user/medication/add");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "El usuario ya tiene ese medicamento asociado.");
	        return "medication/userMedicationTemplate";
	    }

	    Medication newMedication = medicationService.addMedication(medication);
	    model.addAttribute("msgClass", "alert-success");
	    model.addAttribute("msg", "El medicamento '" + newMedication.getDescription() + "' ha sido añadido con éxito.");

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    return "user/userForm";
	}
	
	// GET delete
	@GetMapping("/user/medication/delete/{id}")
	public String deleteUserMedication(Model model, @PathVariable String id) {
	    Medication medication = validateAndGetMedication(id, model);
	    if (medication == null) {
	        return handleError(model, "Error al obtener el medicamento.", "error");
	    }

	    User user = medication.getUser();
	    
	    setupUserModel(model, user, "delete", false, "Eliminar medicamento", "/user/medication/delete");
	    model.addAttribute("medication", medication);
	    
	    return "medication/userMedicationTemplate";
	}
	
	// POST delete
	@PostMapping("/user/medication/delete")
	public String deleteUserMedicationPost(Model model, @ModelAttribute Medication medication, @RequestParam Integer medicationUserId) throws UserException {
	    User user = validateAndGetUser(medicationUserId.toString(), model);
	    
	    if (user == null) {
	        return handleError(model, "El usuario asociado al medicamento no es válido.", "error");
	    }
	    

	    Optional<Medication> optionalMedication = medicationService.getMedicationById(medication.getId());
        Medication medicationToDelete = optionalMedication.get();
	    
	    if (medicationToDelete == null) {
	        return handleError(model, "Error: No se pudo eliminar el medicamento. No existe en la base de datos.", "error");
	    }

	    medicationService.deleteMedication(medicationToDelete);
	    model.addAttribute("msgClass", "alert-success");
	    model.addAttribute("msg", "Medicamento '" + medicationToDelete.getDescription() + "' borrado con éxito.");

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    return "user/userForm";
	}
	
	// GET edit
	@GetMapping("/user/medication/edit/{id}")
	public String editUserMedication(Model model, @PathVariable String id) {
	    Medication medication = validateAndGetMedication(id, model);
	    
	    if (medication == null) {
	        return handleError(model, "Error al obtener el medicamento.", "error");
	    }

	    User user = medication.getUser();
	    
	    setupUserModel(model, user, "edit", true, "Editar medicamento", "/user/medication/edit");
	    model.addAttribute("medication", medication);
	    
	    return "medication/userMedicationTemplate";
	}
	
	// POST edit
	@PostMapping("/user/medication/edit")
	public String editUserMedicationPost(Model model, @Validated @ModelAttribute Medication medication, BindingResult bindingResult, @RequestParam Integer medicationUserId) throws UserException {
	    
		User user = validateAndGetUser(medicationUserId.toString(), model);
		
	    if (user == null) {
	        return handleError(model, "El usuario asociado al medicamento no es válido.", "error");
	    }

	    medication.setUser(user);
	    model.addAttribute("user", user);

	    if (bindingResult != null && bindingResult.hasErrors()) {
	        setupUserModel(model, user, "edit", true, "Editar medicamento", "/user/medication/edit");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "Por favor, corrige los errores en el formulario.");
	        return "medication/userMedicationTemplate";
	    }

	    if (medicationService.medicationExists(medication)) {
	        setupUserModel(model, user, "edit", true, "Editar medicamento", "/user/medication/edit");
	        model.addAttribute("msgClass", "alert-danger");
	        model.addAttribute("msg", "El usuario ya tiene ese medicamento asociado.");
	        return "medication/userMedicationTemplate";
	    }

	    medicationService.editMedication(medication);
	    model.addAttribute("msgClass", "alert-success");
	    model.addAttribute("msg", "Medicamento '" + medication.getDescription() + "' editado con éxito.");

	    setupUserModel(model, user, "show", true, "Detalles del usuario", "");
	    return "user/userForm";
	}

}
