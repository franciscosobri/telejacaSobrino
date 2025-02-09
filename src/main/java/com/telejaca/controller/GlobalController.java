package com.telejaca.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        String path = request.getRequestURI(); // Obtiene el path actual de la solicitud
        
     // Determinar el valor de currentPath basado en la ruta
        if (path.startsWith("/user/")) {
            if (path.contains("/calls")) {
                model.addAttribute("currentPath", "llamadas");
                model.addAttribute("miniNav", "Llamadas");
            } else if (path.contains("/phones")) {
                model.addAttribute("currentPath", "telefonos");
                model.addAttribute("miniNav", "Teléfonos");
            } else if (path.contains("/medicationList")) {
                model.addAttribute("currentPath", "medicamentos");
                model.addAttribute("miniNav", "Medicamentos");
            } else if (path.contains("/careGivers")) {
	            model.addAttribute("currentPath", "relaciones");
	            model.addAttribute("miniNav", "Relaciones");
            } else {
                model.addAttribute("currentPath", "detalles");
                model.addAttribute("miniNav", "");
            }
        } else {
        	// Si no es de usuarios mando el path completo para usarlo en el nav superior
        	model.addAttribute("currentPath", path);
        }
    }
}