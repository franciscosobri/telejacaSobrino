package com.telejaca.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="Telefono")
public class Phone {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="telefono")
	@NotBlank(message = "El número de teléfono es obligatorio")
	@Pattern(regexp = "^[6-9][0-9]{8}$", message = "El número de teléfono debe contener 9 dígitos, empezando por 6, 7, 8 o 9")
	private String number;
	
	@Column(name="descripcion")
	private String description;
	
	@ManyToOne
	//@NotNull(message = "Selecciona un usuario válido de la lista")
	@JoinColumn(name="usuario_id") //nombre de la columna que es FK
	private User phoneUser;

	
	public Phone() {
		super();
	}
	
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public User getPhoneUser() {
		return phoneUser;
	}


	public void setPhoneUser(User phoneUser) {
		this.phoneUser = phoneUser;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, number, phoneUser);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phone other = (Phone) obj;
		return Objects.equals(id, other.id) && Objects.equals(number, other.number)
				&& Objects.equals(phoneUser, other.phoneUser);
	}	

	
}
