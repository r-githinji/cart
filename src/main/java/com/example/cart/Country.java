package com.example.cart;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Cacheable;
import jakarta.validation.constraints.NotEmpty;

@Cacheable(false)
@Entity
@Table(name = "ctry")
public class Country implements Serializable {

	private static final long serialVersionUID = 8203862536L;
	private Integer id;
	private String code, name;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ctry_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotEmpty(message = "{country.code.isempty}")
	@Column(name = "ctry_code", unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@NotEmpty(message = "country.name.isempty")
	@Column(name = "ctry_name", unique = true, nullable = false, updatable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
	
	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Country)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Country that = (Country)other;
		return code.contentEquals(that.getCode());
	}
}
