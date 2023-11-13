package com.example.cart;

import java.util.Objects;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Cacheable(false)
@Entity
@Table(name = "sle_ln")
public class SaleLine implements Serializable {

	private static final long serialVersionUID = 9325723782L;
	private Integer id;
	private Item item;
	private Integer quantity;
	private Double cost, gross = 0.0, nett = 0.0, tax = 0.0;
	private LocalDateTime created; 
	private Sale sale;
	
	public SaleLine() {		
	}
	
	public SaleLine(Item item, Integer quantity) {
	    	this.item = item;
	    	this.quantity = quantity;
	    	created = LocalDateTime.now();
	    	cost = item.getSellPrice();
	    	//value added tax
	    	tax = BigDecimal.valueOf((cost * 1.16) - cost).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@Id
   	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ln_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotNull
	@OneToOne
	@JoinColumn(name = "ln_itm")
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	@NotNull
	@Positive
	@Column(name = "ln_cost", nullable = false, updatable = false)
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	@NotNull
	@Positive
	@Column(name = "ln_qty", nullable = false, updatable = false)
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer units) {
		this.quantity = units;
	}
	@NotNull
	@PositiveOrZero
	@Column(name ="ln_nett", nullable = false, updatable = false)
	public Double getNett() {
		return nett;
	}
	public void setNett(Double nett) {
		this.nett = nett;
	}
	@NotNull
	@PositiveOrZero
	@Column(name = "ln_gross", nullable = false, updatable = false)
	public Double getGross() {
		return gross;
	}
	public void setGross(Double gross) {
		this.gross = gross;
	}
	@NotNull
	@PositiveOrZero
	@Column(name = "ln_tax", nullable = false, updatable = false)
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	@NotNull
	@Column(name = "ln_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@JsonBackReference(value = "lines")
	@NotNull
	@ManyToOne
	@JoinColumn(name = "ln_sle")
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	
	public void aggregate() {
		nett = cost * quantity;
		gross = nett + tax;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sale, item);
	}
	
	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof SaleLine)) {
			return false;
		} else if (this == other) {
			return true;
		}
		SaleLine that = (SaleLine)other;
		return item.equals(that.getItem()) && sale.equals(that.getSale());
	}
}
