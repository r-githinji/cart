package com.example.cart;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Cacheable(false)
@Entity
@Table(name = "addr")
public class Address implements Serializable {

	private static final long serialVersionUID = 4850192209L;
	private Integer id;
	private String street, avenue, houseNo, city;
	private Boolean remember;
	private Integer sequence = 1;
	private Customer owner; 

	public Address() {
	}
	
	public Address(Customer owner) {
		this.owner = owner;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "addr_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotEmpty(message = "{address.street.isempty}")
	@Column(name = "addr_street", nullable = false, updatable = false)
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	@NotEmpty(message = "{address.avenue.isempty}")
	@Column(name = "addr_avenue", nullable = false, updatable = false)
	public String getAvenue() {
		return avenue;
	}
	public void setAvenue(String avenue) {
		this.avenue = avenue;
	}
	@NotEmpty(message = "{address.houseno.isempty}")
	@Column(name = "addr_hseno", nullable = false, updatable = false)
	public String getHouseNo() {
		return houseNo;
	}
	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}
	@NotEmpty(message = "{address.city.isempty}")
	@Column(name = "addr_city", nullable = false, updatable = false)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@NotNull
	@Column(name = "addr_remember", nullable = false, updatable = false)
	public Boolean getRemember() {
		return remember;
	}
	public void setRemember(Boolean remember) {
		this.remember = remember;
	}
	@NotNull
	@Column(name = "addr_seq", nullable = false)
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	@JsonBackReference
	@NotNull
	@ManyToOne
	@JoinColumn(name = "addr_cus")
	public Customer getOwner() {
		return owner;
	}
	public void setOwner(Customer owner) {
		this.owner = owner;
	} 

	@Override
	public int hashCode() {
		return Objects.hash(owner, sequence);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Address)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Address addr = (Address)other;
		return owner.equals(addr.getOwner()) && sequence.compareTo(addr.getSequence()) == 0;
	}
}
