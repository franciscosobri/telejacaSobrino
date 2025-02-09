package com.telejaca.controller;

import com.telejaca.model.Employee;
import com.telejaca.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String listEmployees(@RequestParam(value = "msg", required = false) String msg, Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees()); 
        model.addAttribute("msg", msg);
        return "employee/employeeList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("h1", "Añadir Nuevo Empleado");
        return "employee/employeeForm";
    }

    @PostMapping
    public String saveEmployee(@ModelAttribute("employee") @Valid Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "employee/employeeForm";
        }
        Optional<Employee> existingEmployee = employeeService.getEmployeeByUsername(employee.getUsername());
        if (existingEmployee.isPresent()) {
            model.addAttribute("msg", "El nombre de usuario ya existe");
            return "employee/employeeForm";
        }
        employeeService.saveEmployee(employee);
        return "redirect:/employee?msg=Empleado+añadido+correctamente";
    }

    @GetMapping("/edit/{username}")
    public String showEditForm(@PathVariable String username, Model model) {
        Optional<Employee> employee = employeeService.getEmployeeByUsername(username);
        if (employee.isEmpty()) {
            return "redirect:/employee?msg=Empleado+no+encontrado";
        }
        model.addAttribute("employee", employee.get());
        model.addAttribute("h1", "Editar Empleado");
        return "employee/employeeForm";
    }

    @PostMapping("/edit/{username}")
    public String editEmployee(@PathVariable String username, @ModelAttribute("employee") @Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            return "employee/employeeForm";
        }
        employee.setUsername(username);
        employeeService.updateEmployee(employee);
        return "redirect:/employee?msg=Empleado+actualizado+correctamente";
    }

    @GetMapping("/delete/{username}")
    public String deleteEmployee(@PathVariable String username, Model model) {
        employeeService.deleteEmployee(username);
        model.addAttribute("h1", "Eliminar Empleado");
        return "redirect:/employee?msg=Empleado+eliminado+correctamente";
    }
}
