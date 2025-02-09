package com.telejaca.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Empleados")
public class Employee {

    @Id
    @Column(name = "username", length = 50, nullable = false)
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 50, message = "El número máximo de caracteres permitido es 50.")
    private String username;

    @Column(name = "nombre", length = 50, nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El número máximo de caracteres permitido es 50.")
    private String name;

    @Column(name = "apellido", length = 50, nullable = false)
    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 50, message = "El número máximo de caracteres permitido es 50.")
    private String lastName;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @Column(name = "roles", length = 50, nullable = false)
    @NotBlank(message = "Debe especificar un rol")
    private String roles;
    
	@OneToMany(mappedBy="employee")
	private List<Call> callList;

    public Employee(){
		super();
		this.callList= new ArrayList<Call>();
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }


	public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
    
    public List<Call> getCallList() {
		return callList;
	}

	public void setCallList(List<Call> callList) {
		this.callList = callList;
	}
	

	@Override
    public int hashCode() {
    	return Objects.hash(username);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	if (getClass() != obj.getClass())
    		return false;
    	Employee other = (Employee) obj;
    	return Objects.equals(username, other.username);
    }
}
