package com.telejaca.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class CallId implements Serializable {

	private static final long serialVersionUID = -8369025346488331727L;

	private Integer user;
	
	private String employee;
	
	private Integer callType;
	
	private LocalDate date;
	
	private Integer order;
	
	public CallId() {
		super();
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public Integer getCallType() {
		return callType;
	}

	public void setCallType(Integer callType) {
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

	public static long getSerialversionuid() {
		return serialVersionUID;
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
		CallId other = (CallId) obj;
		return Objects.equals(callType, other.callType) && Objects.equals(date, other.date)
				&& Objects.equals(employee, other.employee) && Objects.equals(order, other.order)
				&& Objects.equals(user, other.user);
	}
	

}
