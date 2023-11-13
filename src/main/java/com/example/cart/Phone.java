package com.example.cart;

import java.util.Objects;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Cacheable;
import jakarta.persistence.GenerationType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Cacheable(false)
@Entity
@Table(name = "phone")
public class Phone implements Serializable {

	private static final long serialVersionUID = 90237834867543882L;
	private Integer id;
	private Country area;
	private String msisdn;
	private Customer owner;
	
	public Phone() {		
	}
	
	public Phone(Customer owner) {
		this.owner = owner;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ph_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Valid
	@NotNull(message = "{phone.area.isnull}")
	@ManyToOne
	@JoinColumn(name = "ph_code")
	public Country getArea() {
		return area;
	}
	public void setArea(Country area) {
		this.area = area;
	}
	@Pattern(regexp = "^([0-9]{9})$", message = "{phone.msisdn.pattern}")
	@Column(name = "ph_no", nullable = false)
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String value) {
		this.msisdn = value;
	}
	@JsonBackReference(value = "phones")
	@NotNull
	@ManyToOne
	@JoinColumn(name = "ph_cus")
	public Customer getOwner() {		
		return owner;
	}
	public void setOwner(Customer owner) {
		this.owner = owner;
	}
	
	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Phone)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Phone that = (Phone)other;
		return area.equals(that.getArea()) && msisdn.contentEquals(that.getMsisdn()) && owner.equals(that.getOwner());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(owner , area, msisdn);
	}
	
	public String format() {
		return String.format("%s%s", null == area ? "" : area.getCode(), null == msisdn ? "" : msisdn);
	}
}
