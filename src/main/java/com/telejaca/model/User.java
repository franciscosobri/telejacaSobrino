package com.telejaca.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="Usuarios")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank (message ="El nombre no puede ser estar vacío")
	@Column(name="nombre")
	@Size(max=100, message ="El número máximo de caracteres permitido es de 100.")
	private String name;
	
	@Column(name="apellido")
	@Size(max=150, message ="El número máximo de caracteres permitido es de 150.")
	private String surname;
	
	@Column(name="genero")
	private Character gender;
	
	@Column(name="fecha_nacimiento")
	@Past(message ="La fecha de nacimiento debe ser anterior a la fecha actual.")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate birthday;
	
	@Column(name="direccion")
	@Size(max=225, message ="El número máximo de caracteres permitido para este campo es de 225")
	private String address;
	
	@Column(name="datos_importantes")
	@Size(max=65535, message ="Se ha superado el número de caracteres permitido.")
	private String relevantInformation;
	
	@Column(name="unidad_de_convivencia")
	@Size(max=225, message ="El número máximo de caracteres permitido es de 225")
	private String householdMembers;
	
//	@Column(name="localidad_id")
//	private Integer localityId; /*No puedo enlazar aquí directamente con la clase Locality o algo así? */
	
	@Column(name="causa_baja")
	@Size(max=225, message ="El número máximo de caracteres permitido es de 225")
	private String cancellationCause;
	
	@Column(name="fecha_baja")
	private LocalDate cancellationDate;
	
	@OneToMany(mappedBy="user") //nombre de cómo he llamado el atributo en el otro lado
	private List<Medication> medicationList;

	@OneToMany(mappedBy="caregiver")
	private List<CaregiverRelation> userCaregiverRelation;
	
	@OneToMany(mappedBy="recipentUser")
	private List<CaregiverRelation> userCareeRelation;
	
	@OneToMany(mappedBy = "phoneUser", cascade = CascadeType.ALL, orphanRemoval = true)
	@Valid
	private List<Phone> phoneList = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="localidad_id")
	private Locality locality;
	
	@OneToMany(mappedBy="user")
	private List<Call> callList;
	

	public User() {
		super();
		this.medicationList = new ArrayList<Medication>();
		this.userCareeRelation = new ArrayList<CaregiverRelation>();
		this.userCareeRelation = new ArrayList<CaregiverRelation>();
		this.callList = new ArrayList<Call>();
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRelevantInformation() {
		return relevantInformation;
	}

	public void setRelevantInformation(String relevantInformation) {
		this.relevantInformation = relevantInformation;
	}

	public String getHouseholdMembers() {
		return householdMembers;
	}

	public void setHouseholdMembers(String householdMembers) {
		this.householdMembers = householdMembers;
	}

//	public Integer getLocalityId() {
//		return localityId;
//	}
//
//	public void setLocalityId(Integer localityId) {
//		this.localityId = localityId;
//	}

	public String getCancellationCause() {
		return cancellationCause;
	}

	public void setCancellationCause(String cancellationCause) {
		this.cancellationCause = cancellationCause;
	}

	public LocalDate getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(LocalDate cancellationDate) {
		this.cancellationDate = cancellationDate;
	}
	
	public List<Medication> getMedicationList() {
		return medicationList;
	}

	public void setMedicationList(List<Medication> medicationList) {
		this.medicationList = medicationList;
	}

	public List<CaregiverRelation> getUserCaregiverRelation() {
		return userCaregiverRelation;
	}

	public void setUserCaregiverRelation(List<CaregiverRelation> userCaregiverRelation) {
		this.userCaregiverRelation = userCaregiverRelation;
	}

	public List<CaregiverRelation> getUserCareeRelation() {
		return userCareeRelation;
	}

	public void setUserCareeRelation(List<CaregiverRelation> userCareeRelation) {
		this.userCareeRelation = userCareeRelation;
	}

	public List<Phone> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<Phone> phoneList) {
		this.phoneList = phoneList;
	}
	
	public List<Call> getCallList() {
		return callList;
	}

	public void setCallList(List<Call> callList) {
		this.callList = callList;
	}
	public Locality getLocality() {
		return locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object o) {
		return o==this || (o!=null && o instanceof User && o.hashCode()==this.hashCode());
	}

}
