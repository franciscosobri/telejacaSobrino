package com.telejaca.service;

import com.telejaca.model.Employee;
import com.telejaca.repository.EmployeeRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class EmployeeService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void saveEmployee(Employee employee) {
        // Validar si el email ya está en uso por otro usuario
        if (isEmailAlreadyInUse(employee.getEmail(), employee.getUsername())) {
            throw new IllegalArgumentException("Ya existe un empleado con este correo electrónico.");
        }

        // Validar si el username ya está en uso
        if (existsByUsername(employee.getUsername())) {
            throw new IllegalArgumentException("Ya existe un empleado con este nombre de usuario.");
        }

        // Si no tiene contraseña, generar una aleatoria
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            employee.setPassword(generateRandomPassword());
        }
        
        try {
			sendVerificationEmail(employee, employee.getPassword());
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

        // Encriptar la contraseña antes de guardarla
        employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployeeByUsername(String username) {
        return employeeRepository.findById(username);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public void updateEmployee(Employee employee) {
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getUsername());
        if (existingEmployeeOpt.isPresent()) {
            Employee existingEmployee = existingEmployeeOpt.get();

            // Verificar si el email está en uso por otro empleado
            if (isEmailAlreadyInUse(employee.getEmail(), employee.getUsername())) {
                throw new IllegalArgumentException("Ya existe un empleado con este correo electrónico.");
            }

            // Actualizar datos del empleado
            existingEmployee.setName(employee.getName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmail(employee.getEmail());
            if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
                existingEmployee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
            }
            existingEmployee.setRoles(employee.getRoles());
            employeeRepository.save(existingEmployee);
        } else {
            // Si no existe, guardar como nuevo
            saveEmployee(employee);
        }
    }

    public void deleteEmployee(String username) {
        employeeRepository.deleteById(username);
    }

    public boolean isEmailAlreadyInUse(String email, String username) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(email);
        // Si el email pertenece a otro usuario (diferente username), es duplicado
        return existingEmployee.isPresent() && !existingEmployee.get().getUsername().equals(username);
    }

    public boolean existsByUsername(String username) {
        return employeeRepository.existsByUsername(username);
    }

    private String generateRandomPassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    public boolean hasAdminRole() {
    	return ((Employee)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains("ADMIN");    		
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = employeeRepository.findByUsername(username);

        if (e == null) throw new UsernameNotFoundException("Usuario no encontrado");

        return e;
    }
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendVerificationEmail(Employee user, String clave) throws MessagingException, UnsupportedEncodingException {
    	 String toAddress = user.getEmail();
    	 String fromAddress = "jacaranda.telejaca@gmail.com";
    	 String senderName = "Jacaranda";
    	 String subject = "¡Alta exitosa en la aplicación!";
    	 
    	 // Contenido del mail
    	 String content = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>"
                 + "<h2 style='color: #0056b3;'>¡Bienvenido a Telejaca, " + user.getName() + "!</h2>"
                 + "<p>Estamos encantados de darte la bienvenida a nuestra plataforma.</p>"
                 + "<p><strong>Usuario generado:</strong> <span style='color: #007bff;'>" + user.getUsername() + "</span></p>"
                 + "<p><strong>Contraseña generada:</strong> <span style='color: #28a745; font-weight: bold;'>" + clave + "</span></p>"
                 + "<p>Por favor, inicia sesión lo antes posible y cambia tu contraseña por una más segura.</p>"
                 + "<hr style='border: 1px solid #ddd;'>"
                 + "<p style='font-size: 12px; color: #777;'>Si no solicitaste este registro, ignora este mensaje.</p>"
                 + "</div>";
    	 
    	 MimeMessage message = mailSender.createMimeMessage();
    	 MimeMessageHelper helper = new MimeMessageHelper(message, true);
    	 helper.setFrom(fromAddress, senderName);
    	 helper.setTo(toAddress);
    	 helper.setSubject(subject);
    	 helper.setText(content, true);
    	 mailSender.send(message);
    }
    
}
