package com.telejaca.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Es_Cuidador")
@IdClass(CaregiverRelationId.class)
public class CaregiverRelation {

	@Id
	@ManyToOne
	@JoinColumn(name="cuidador_id")
	private User caregiver;
	
	@Id
	@ManyToOne
	@JoinColumn(name="usuario_receptor_id")
	private User recipentUser;
	
	public CaregiverRelation() {
		super();
	}

	
	public User getCaregiver() {
		return caregiver;
	}


	public void setCaregiver(User caregiver) {
		this.caregiver = caregiver;
	}


	public User getRecipentUser() {
		return recipentUser;
	}


	public void setRecipentUser(User recipentUser) {
		this.recipentUser = recipentUser;
	}


	@Override
	public int hashCode() {
		return Objects.hash(recipentUser, caregiver);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaregiverRelation other = (CaregiverRelation) obj;
		return Objects.equals(recipentUser, other.recipentUser) && Objects.equals(caregiver, other.caregiver);
	}
	
	
	

}
