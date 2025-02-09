package com.telejaca.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.telejaca.model.Locality;
import com.telejaca.service.LocalityService;

import jakarta.validation.Valid;

@Controller
public class LocalityController {

    @Autowired
    private LocalityService localityService;

    private boolean isNumeric(String str) {
        return str != null && str.matches("[0-9]+");
    }

    @GetMapping("/locality")
    public String localityList(@RequestParam(value = "msg", required = false) String msg, Model model) {
        List<Locality> localities = localityService.getAllLocalitys();
        model.addAttribute("localities", localities);
        if (msg != null) {
            model.addAttribute("msg", msg);
        }
        return "locality/localityList";
    }

    @GetMapping("/locality/show/{id}")
    public String showLocality(@PathVariable String id, @RequestParam(value = "msg", required = false) String msg, Model model) {
        if (!isNumeric(id)) {
            return "redirect:/locality?msg=El+ID+introducido+no+es+válido";
        }
        Long localityId = Long.parseLong(id);
        Optional<Locality> localityOpt = localityService.getLocalityById(localityId);
        if (localityOpt.isEmpty()) {
            return "redirect:/locality?msg=El+ID+introducido+no+existe";
        }
        model.addAttribute("locality", localityOpt.get());
        model.addAttribute("h1", "Detalles localidad");
        model.addAttribute("buttonText", "Mostrar");
        if (msg != null) {
            model.addAttribute("msg", msg);
        }
        return "locality/localityForm";
    }


    @GetMapping("/locality/add")
    public String addLocality(Model model) {
        model.addAttribute("locality", new Locality());
        model.addAttribute("buttonText", "Enviar");
        model.addAttribute("h1", "Añadir localidad");
        return "locality/localityForm";
    }

    @PostMapping("/locality")
    public String saveOrUpdateLocality(@ModelAttribute("locality") @Valid Locality locality, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("msg", "Error al guardar la localidad");
            model.addAttribute("buttonText", locality.getId() == null ? "Enviar" : "Editar");
            return "locality/localityForm";
        }

        // Verificar si el nombre ya existe
        Optional<Locality> existingLocality = localityService.getLocalityByName(locality.getName());
        if (existingLocality.isPresent() && (locality.getId() == null || !existingLocality.get().getId().equals(locality.getId()))) {
            model.addAttribute("msg", "Ya existe una localidad con este nombre");
            model.addAttribute("buttonText", locality.getId() == null ? "Enviar" : "Editar");
            return "locality/localityForm";
        }

        boolean isEdit = locality.getId() != null;
        localityService.saveLocality(locality);

        String message = isEdit ? "editado" : "agregado";
        if (isEdit) {
            return "redirect:/locality/show/" + locality.getId() + "?msg=Localidad+editada+correctamente";
        } else {
            return "redirect:/locality?msg=Localidad+" + message + "+correctamente";
        }
    }

    @GetMapping("/locality/edit/{id}")
    public String showFormEdit(@PathVariable String id, Model model) {
        if (!isNumeric(id)) {
            return "redirect:/locality?msg=El+ID+introducido+no+es+correcto";
        }
        Long localityId = Long.parseLong(id);
        Optional<Locality> localityOpt = localityService.getLocalityById(localityId);
        if (localityOpt.isEmpty()) {
            return "redirect:/locality?msg=El+ID+introducido+no+es+existe";
        }
        model.addAttribute("locality", localityOpt.get());
        model.addAttribute("h1", "Editar localidad");
        model.addAttribute("buttonText", "Editar");
        return "locality/localityForm";
    }

    @GetMapping("/locality/delete/{id}")
    public String showFormDelete(@PathVariable String id, Model model) {
        if (!isNumeric(id)) {
            return "redirect:/locality?msg=El+ID+introducido+no+es+correcto";
        }
        Long localityId = Long.parseLong(id);
        Optional<Locality> localityOpt = localityService.getLocalityById(localityId);
        if (localityOpt.isEmpty()) {
            return "redirect:/locality?msg=El+ID+introducido+no+existe";
        }
        model.addAttribute("locality", localityOpt.get());
        model.addAttribute("h1", "Eliminar localidad");
        model.addAttribute("buttonText", "Eliminar");
        return "locality/localityForm";
    }

    @PostMapping("/locality/deleteConfirm")
    public String confirmDelete(@ModelAttribute("locality") Locality locality) {
        try {
            localityService.deleteLocality(locality.getId());
            return "redirect:/locality?msg=Localidad+eliminada+correctamente";
        } catch (Exception e) {
            return "redirect:/locality?msg=Error+al+eliminar+la+localidad";
        }
    }
}
