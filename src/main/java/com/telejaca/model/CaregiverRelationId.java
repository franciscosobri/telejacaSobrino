package com.telejaca.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.IdClass;

public class CaregiverRelationId implements Serializable {

	private static final long serialVersionUID = 3197250255977742115L;

	private Integer caregiver;
	
	private Integer recipentUser;

	public CaregiverRelationId() {
		super();
	}
	
	public Integer getCaregiver() {
		return caregiver;
	}

	public void setCaregiver(Integer caregiver) {
		this.caregiver = caregiver;
	}

	public Integer getRecipentUser() {
		return recipentUser;
	}

	public void setRecipentUser(Integer recipentUser) {
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
		CaregiverRelationId other = (CaregiverRelationId) obj;
		return Objects.equals(recipentUser, other.recipentUser) && Objects.equals(caregiver, other.caregiver);
	}
	
	

}
