package com.telejaca.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.telejaca.model.Employee;
import com.telejaca.service.EmployeeService;
import jakarta.validation.Valid;
import net.bytebuddy.utility.RandomString;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/employee")
    public String listEmployees(@RequestParam(required = false) String msg, Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("msg", msg);
        return "employee/employeeList";
    }

    @GetMapping("/employee/show/{username}")
    public String showEmployee(@PathVariable String username, @RequestParam(required = false) String msg, Model model) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeByUsername(username);
        if (employeeOpt.isEmpty()) {
            return "redirect:/employee?msg=El+username+introducido+no+existe";
        }
        model.addAttribute("employee", employeeOpt.get());
        model.addAttribute("h1", "Detalles de " + employeeOpt.get().getName() + " " + employeeOpt.get().getLastName());
        model.addAttribute("buttonText", "Mostrar");
        if (msg != null) {
            model.addAttribute("msg", msg);
        }
        return "employee/employeeForm";
    }

    @GetMapping("/employee/add")
    public String addEmployee(Model model) {
        Employee employee = new Employee();
        employee.setRoles("USER"); // Asigna el rol 'USER' por defecto
        model.addAttribute("employee", employee);
        model.addAttribute("buttonText", "Enviar");
        model.addAttribute("h1", "Añadir Empleado");
        return "employee/employeeForm";
    }

    @PostMapping("/employee")
    public String saveOrUpdateEmployee(@ModelAttribute @Valid Employee employee, 
                                        BindingResult result, Model model) {
        // Determinar si estamos en modo "editar" o "añadir"
        boolean isEditMode = employee.getUsername() != null && !employee.getUsername().isEmpty();

        if (result.hasErrors()) {
            System.out.println("Errores de validación: " + result.getAllErrors());
            model.addAttribute("msg", "Error al guardar el empleado");
            model.addAttribute("buttonText", isEditMode ? "Editar" : "Enviar");
            model.addAttribute("activity", isEditMode ? "edit" : "add"); // Indicar el modo actual
            model.addAttribute("h1", isEditMode ? "Editar Empleado" : "Añadir Empleado");
            return "employee/employeeForm";
        }

        // Validar si el email está en uso
        if (employeeService.isEmailAlreadyInUse(employee.getEmail(), employee.getUsername())) {
            model.addAttribute("msg", "Ya existe un empleado con este correo electrónico");
            model.addAttribute("buttonText", isEditMode ? "Editar" : "Enviar"); // Conservar el modo actual
            model.addAttribute("activity", isEditMode ? "edit" : "add"); // Indicar el modo actual
            model.addAttribute("h1", isEditMode ? "Editar Empleado" : "Añadir Empleado"); // Conservar el modo actual
            return "employee/employeeForm";
        }

        // Validar si el username ya está en uso (solo aplica en modo "añadir")
        if (!isEditMode && employeeService.existsByUsername(employee.getUsername())) {
            model.addAttribute("msg", "El nombre de usuario ya existe.");
            model.addAttribute("buttonText", "Enviar"); // Modo "añadir"
            model.addAttribute("activity", "add"); // Indicar el modo "añadir"
            model.addAttribute("h1", "Añadir Empleado"); // Modo "añadir"
            return "employee/employeeForm";
        }

        // Guardar o actualizar empleado
        if (isEditMode) {
            String roles = employee.getRoles().replace("_", ",");
            employee.setRoles(roles);
            employeeService.updateEmployee(employee);
            if (this.employeeService.hasAdminRole()) {
                return "redirect:/employee?msg=Empleado+editado+correctamente";
            } else {
                return "redirect:/index?msg=Empleado+editado+correctamente";
            }
        } else {
            employee.setUsername(employee.getName().substring(0, 3).toLowerCase() + employee.getLastName().substring(0, 3).toLowerCase() + employeeService.getAllEmployees().size());
            String randomCode = RandomString.make(10);
            employee.setPassword(randomCode);
            employeeService.saveEmployee(employee);

            // Logs para depuración
            System.out.println("Formulario enviado con:");
            System.out.println("Username: " + employee.getUsername());
            System.out.println("Email: " + employee.getEmail());

            if (this.employeeService.hasAdminRole()) {
                return "redirect:/employee?msg=Empleado+agregado+correctamente";
            } else {
                return "redirect:/index?msg=Empleado+agregado+correctamente";
            }
        }
    }


    @GetMapping("/employee/edit/{username}")
    public String showFormEdit(@PathVariable String username, Model model) {
       Optional<Employee> employeeOpt = employeeService.getEmployeeByUsername(username);
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
        if (employeeOpt.isEmpty()) {
            if(this.employeeService.hasAdminRole()) {
            	return "redirect:/employee?msg=Empleado+editado+correctamente";            	
            }else {
            	return "redirect:/index?msg=Empleado+editado+correctamente"; 
            }
        }else if(!this.employeeService.hasAdminRole() && !employeeOpt.orElse(null).getUsername().equals(authentication.getName())) {
        	model.addAttribute("msg", "Acceso restringido a usuarios con rol de administrador.");
        	return "error";
        }
        model.addAttribute("activity", "edit");
        model.addAttribute("employee", employeeOpt.get());
        model.addAttribute("h1", "Editar " + employeeOpt.get().getName() + " " + employeeOpt.get().getLastName());
        model.addAttribute("buttonText", "Editar");
        return "employee/employeeForm";
    }

    @PostMapping("/employee/edit/{username}")
    public String editEmployee(Model model, @PathVariable String username, @ModelAttribute @Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
        	model.addAttribute("activity", "edit");
            return "employee/employeeForm";
        }
        employeeService.updateEmployee(employee);
        if(this.employeeService.hasAdminRole()) {
        	return "redirect:/employee?msg=Empleado+editado+correctamente";            	
        }else {
        	return "redirect:/index?msg=Empleado+editado+correctamente"; 
        }
    }

    @GetMapping("/employee/delete/{username}")
    public String showFormDelete(@PathVariable String username, Model model) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeByUsername(username);
        if (employeeOpt.isEmpty()) {
            return "redirect:/employee?msg=El+username+introducido+no+existe";
        }
        model.addAttribute("employee", employeeOpt.get());
        model.addAttribute("h1", "Eliminar " + employeeOpt.get().getName() + " " + employeeOpt.get().getLastName());
        model.addAttribute("buttonText", "Eliminar");
        return "employee/employeeForm";
    }

    @PostMapping("/employee/deleteConfirm")
    public String confirmDelete(@ModelAttribute Employee employee) {
        try {
            employeeService.deleteEmployee(employee.getUsername());
            return "redirect:/employee?msg=Empleado+eliminado+correctamente";
        } catch (DataAccessException e) {
            return "redirect:/employee?msg=Error+al+eliminar+el+empleado+en+la+base+de+datos";
        } catch (Exception e) {
            return "redirect:/employee?msg=Error+inesperado+al+eliminar+el+empleado";
        }
    }
}
