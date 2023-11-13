package com.example.cart;

import java.io.Serializable;

import java.util.List;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Cacheable(false)
@Entity
@Table(name = "cus")
public class Customer implements Serializable {
    
	private static final long serialVersionUID = 8003563346L;
	private Integer id;
	private LocalDateTime created;
	private String firstName, lastName, reference, email;
	private List<Address> addresses;
	private List<Phone> phones;
	private GenderType gender; 
	private boolean active;

	public Customer() {		
	}
	
	public Customer(String reference) {
		this.reference = reference;
		active = true;
		created = LocalDateTime.now();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cus_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotEmpty(message = "{customer.reference.isempty}")
	@Column(name = "cus_ref", unique = true, nullable = false, updatable = false)
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	@NotEmpty(message = "{customer.fname.isempty}")
	@Column(name = "cus_fname", unique = true, nullable = false)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@NotEmpty(message = "{customer.lname.isempty}")
	@Column(name = "cus_lname", unique = true, nullable = false)
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@NotEmpty(message = "{customer.email.isempty}")
	@Column(name = "cus_email", unique = true, nullable = false)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@NotNull
	@Column(name = "cus_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@JsonManagedReference	
	@OneToMany(mappedBy = "owner", cascade = {CascadeType.ALL})
	public List<Address> getAddresses() {
		return null == addresses ? new LinkedList<>() : addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	@Valid
	@Size(min = 1)
	@JsonManagedReference(value = "phones")
	@OneToMany(mappedBy = "owner", cascade = {CascadeType.ALL})	
	public List<Phone> getPhones() {
		return phones;
	}
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}
	@NotNull 
	@Column(name = "cus_gender", nullable = false, updatable = false)
	public GenderType getGender() {
		return gender;
	}
	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	@Column(name = "cus_is_active", nullable = false, updatable = false)
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void add(Address address) {
		if (null == address) {
			return;
		}
		if (null == addresses) {
			addresses = new LinkedList<>();
		}
		address.setOwner(this);
		int max = addresses.stream().mapToInt(Address::getSequence).max().orElse(-1);
		switch (max) {
			case -1:
				max = 1;
			break;
			default: 
				max += 1;
		} 
		address.setSequence(max);
		if (null == addresses.stream().filter(address::equals).findFirst().orElse(null)) {
			addresses.add(address);
		} 
	}

	public boolean remove(Address address) {
		if (null == address) {
			return false;
		} else if (null == addresses || addresses.isEmpty()) {
			return false;
		} 
		return addresses.remove(address);
	}

	public void add(Phone phone) {
		if (null == phone) {
			return;
		}
		if (null == phones) {
			phones = new LinkedList<>();
		}
		phone.setOwner(this);
		if (null == phones.stream().filter(phone::equals).findFirst().orElse(null)) {
			phones.add(phone);
		} 
	}
	
	public boolean remove(Phone phone) {
		if (null == phone) {
			return false;
		} else if (null == phones || phones.isEmpty()) {
			return false;
		} 
		return phones.remove(phone);
	}

	public List<Phone> filterPhones(String expr) {
		return phones.stream().filter(phone -> phone.format().contains(expr) || true).collect(Collectors.toList());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(reference);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Customer)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Customer that = (Customer)other;
		return reference.contentEquals(that.getReference());
	}
}
