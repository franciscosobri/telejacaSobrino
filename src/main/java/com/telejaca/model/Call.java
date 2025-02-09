package com.telejaca.model;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.telejaca.controller.CallId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="Llamada")
@IdClass(CallId.class)
public class Call {
	
	@Id
	@ManyToOne
	@JoinColumn(name="usuario_id")
	private User user;
	
	@Id
	@ManyToOne
	@JoinColumn(name="empleado_username")
	private Employee employee;
	
	@Id
	@ManyToOne
	@NotNull(message="Seleccione un tipo de llamada válido.")
	@JoinColumn(name="tipo_llamada_id")
	private CallType callType;
	
	@Id
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name="fecha")
	private LocalDate date;
	
	@Id
	@Column(name="orden")
	private Integer order;
	
	@Column(name="descripcion")
	@Size(max=65535, min=5, message ="Error. La descripción debe tener al menos 5 caracteres.")
	private String description;
	
	
	public Call() {
		super();
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public Employee getEmployee() {
		return employee;
	}


	public void setEmployee(Employee employee) {
		this.employee = employee;
	}


	public CallType getCallType() {
		return callType;
	}


	public void setCallType(CallType callType) {
		this.callType = callType;
	}


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	public Integer getOrder() {
		return order;
	}


	public void setOrder(Integer order) {
		this.order = order;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public int hashCode() {
		return Objects.hash(callType, date, employee, order, user);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Call other = (Call) obj;
		return Objects.equals(callType, other.callType) && Objects.equals(date, other.date)
				&& Objects.equals(employee, other.employee) && Objects.equals(order, other.order)
				&& Objects.equals(user, other.user);
	}
	

}
