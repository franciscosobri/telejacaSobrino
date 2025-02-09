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

import com.telejaca.model.CallType;
import com.telejaca.service.CallTypeService;


@Controller
public class CallTypeController {
	@Autowired
	CallTypeService callTypeService;
	
	@GetMapping("/callTypes")
	public String listCallTypes(Model model) {
		List<CallType> callTypes = callTypeService.getAllCallTypes();
		model.addAttribute("callTypes", callTypes);
		return "callType/callTypes";
	}
	
	@GetMapping("/callType/show/{id}")
	public String showCallType(Model model, @PathVariable String id) {
		
		if (!id.matches("[0-9]+")) {
	        model.addAttribute("msg", "ID inválido.");
	        List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);
	        return "callType/callTypes";
	    }
		
		Optional<CallType> c = callTypeService.getCallTypeById(Integer.parseInt(id));
		
		if (c.isEmpty()) {
			model.addAttribute("msg", "Tipo de llamada no encontrado.");
			List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);
	        return "callType/callTypes";
		}
		
		if (c.isPresent()) {
			model.addAttribute("callType", c);
		} else {
			model.addAttribute("msg", "Tipo de llamada no encontrada.");
		}
		
		model.addAttribute("activity", "show");
		model.addAttribute("enable", false);
		model.addAttribute("h1", "Ver tipo de llamada");
		model.addAttribute("action", "/callType/show/" + id);
		
		return "callType/callTypeForm";
	}
	
	@GetMapping("/callType/add")
	public String toAdd(Model model) {
		
		model.addAttribute("callType", new CallType());
		model.addAttribute("activity", "add");
		model.addAttribute("enable", true);
		model.addAttribute("h1", "Añadir tipo de llamada");
		model.addAttribute("action", "/callTypeForm/add");

		return "callType/callTypeForm";
	}
	
	@PostMapping("/callTypeForm/add")
	public String addCallType(Model model, @Validated @ModelAttribute CallType callType, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("activity", "add");
	        return "callType/callTypeForm";
	    }

	    if (callTypeService.callTypeExists(callType)) {
	        String message = "El tipo de llamada '" + callType.getDescription() + "' ya existe. Por favor, use otra descripción.";
	        model.addAttribute("msg", message);
	        model.addAttribute("activity", "add");
	        model.addAttribute("h1", "Añadir tipo de llamada");
	        model.addAttribute("enable", true);
	        return "callType/callTypeForm";
	    }

	    try {
	        CallType newCallType = callTypeService.addCallType(callType);
	        if (newCallType == null) {
	            throw new IllegalStateException("Error inesperado al añadir el tipo de llamada.");
	        }
	        String message = "El tipo de llamada '" + newCallType.getDescription() + "' con ID " + newCallType.getId() + " ha sido añadida.";
	        model.addAttribute("msg", message);
	        model.addAttribute("callTypes", callTypeService.getAllCallTypes());
	        return "callType/callTypes";
	    } catch (Exception e) {
	        model.addAttribute("msg", "Ocurrió un error al añadir el tipo de llamada: " + e.getMessage());
	        model.addAttribute("activity", "add");
	        return "callType/callTypeForm";
	    }
	}

	@GetMapping("/callType/delete/{id}")
	public String toDelete(Model model, @PathVariable String id) {
		
		if (!id.matches("[0-9]+")) {
	        model.addAttribute("msg", "ID inválido.");
	        List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);
	        return "callType/callTypes";
	    }
		
		Optional<CallType> c = callTypeService.getCallTypeById(Integer.parseInt(id));
		
		if (c.isEmpty()) {
			model.addAttribute("msg", "Tipo de llamada no encontrado.");
			List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);
	        return "callType/callTypes";
		}
		
	    if (c.isPresent()) {
	        model.addAttribute("callType", c);
	    } else {
	        model.addAttribute("msg", "Tipo de llamada no encontrada.");
	    }

	    model.addAttribute("activity", "delete");
	    model.addAttribute("enable", false);
	    model.addAttribute("h1", "Eliminar tipo de llamada");
	    model.addAttribute("action", "/callType/delete/" + id);

	    return "callType/callTypeForm";
	}
	
	@PostMapping("/callType/delete/{id}")
	public String deleteCallType(Model model, @PathVariable Integer id) {
	    try {
	        Optional<CallType> callTypeToDelete = callTypeService.getCallTypeById(id);
	        if (callTypeToDelete.isPresent()) {
	            callTypeService.deleteCallType(callTypeToDelete.get());
	            model.addAttribute("msg", "Tipo de llamada '" + callTypeToDelete.get().getDescription() + "' borrada con éxito.");
	        } else {
	            model.addAttribute("msg", "Tipo de llamada no encontrado.");
	        }
	    } catch (IllegalArgumentException e1) {
	        model.addAttribute("msg", "Id inapropiado.");
	    } catch (OptimisticLockingFailureException e2) {
	        model.addAttribute("msg", "El tipo de llamada no ha podido ser eliminado.");
	    } catch (Exception e) {
	        model.addAttribute("msg", "Ocurrió un error inesperado.");
	    }

	    List<CallType> callTypes = callTypeService.getAllCallTypes();
	    model.addAttribute("callTypes", callTypes);
	    return "callType/callTypes";
	}
	
	@GetMapping("/callType/edit/{id}")
    public String toEdit(Model model, @PathVariable String id) {
		
		if (!id.matches("[0-9]+")) {
	        model.addAttribute("msg", "ID inválido.");
	        List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);
	        return "callType/callTypes";
	    }
		
		Optional<CallType> c = callTypeService.getCallTypeById(Integer.parseInt(id));
		
		if (c.isEmpty()) {
			model.addAttribute("msg", "Tipo de llamada no encontrado.");
			List<CallType> callTypes = callTypeService.getAllCallTypes();
			model.addAttribute("callTypes", callTypes);
	        return "callType/callTypes";
		}
        
		if (c.isPresent()) {
	        model.addAttribute("callType", c);
	    } else {
	        model.addAttribute("msg", "Tipo de llamada no encontrada.");
	    }

        model.addAttribute("activity", "edit");
        model.addAttribute("h1", "Editar tipo de llamada");
        model.addAttribute("action", "/callType/edit/" + id);
        model.addAttribute("enable", true);
        model.addAttribute("callType", c);

        return "callType/callTypeForm";
    }
	
	@PostMapping("/callType/edit/{id}")
	public String editCallType(Model model, @Validated @ModelAttribute CallType callType, @PathVariable String id, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	        model.addAttribute("activity", "edit");
	        return "callType/callTypeForm";
	    }
		
		if (callTypeService.callTypeExists(callType)) {
	        String message = "El tipo de llamada '" + callType.getDescription() + "' ya existe. Por favor, use otra descripción.";
	        model.addAttribute("msg", message);
	        model.addAttribute("h1", "Editar tipo de llamada");
	        model.addAttribute("activity", "edit");
	        model.addAttribute("enable", true);
	        return "callType/callTypeForm";
	    }

	    CallType editedCallType = callTypeService.editCallType(callType);
	    
	    String message = (editedCallType == null)
	        ? "Error al editar el tipo de llamada."
	        : "El tipo de llamada '" + editedCallType.getDescription() + "' con ID " + editedCallType.getId() + " ha sido editada.";
	    
	    model.addAttribute("msg", message);
	    model.addAttribute("activity", "show");
		model.addAttribute("enable", false);
		model.addAttribute("h1", "Ver tipo de llamada");
		model.addAttribute("action", "/callType/show/" + editedCallType.getId());
		
		return "callType/callTypeForm";
	}

}
