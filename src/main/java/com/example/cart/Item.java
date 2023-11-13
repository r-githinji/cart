package com.example.cart;

import java.io.Serializable;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.math.MathContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Cacheable(false)
@Entity
@Table(name = "itm")
public class Item implements Serializable {
    
	private static final long serialVersionUID = 549670990L;
	private Integer id;
	private Double markup, price, sellPrice, rating = 0.0;
	private LocalDateTime created;
	private String name;
	private Integer buffer;
	private ItemState condition;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "itm_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotNull
	@Column(name = "itm_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@NotNull
	@PositiveOrZero
	@Column(name = "itm_markup", nullable = false, precision = 8)
	public Double getMarkup() {
		return markup;
	}
	public void setMarkup(Double markup) {
		this.markup = markup;
	}
	@NotEmpty
	@Column(name = "itm_name", nullable = false, updatable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@NotNull
	@PositiveOrZero
	@Column(name = "itm_price", nullable = false)
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) { 
		this.price = price;
	}
	@NotNull
	@PositiveOrZero
	@Column(name = "itm_ret_price", nullable = false)
	public Double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}
	@NotNull
	@PositiveOrZero
	@Transient
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	@NotNull
	@Positive(message = "{item.buffer.notpositive}")
	@Column(name = "itm_rsv", nullable = false)
	public Integer getBuffer() {
		return buffer;
	}
	public void setBuffer(Integer buffer) {
		this.buffer = buffer;
	}
	@NotNull(message = "{item.cond.isnull}")
	@Column(name = "itm_cond", nullable = false)
	public ItemState getCondition() {
		return condition;
	}
	public void setCondition(ItemState condition) {
		this.condition = condition;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object other) {        
		if (null == other || !(other instanceof Item)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Item that = (Item)other;  
		return name.contentEquals(that.getName());
	}

	public double price() {
		return price(markup);
	}	
		
	public double price(double rate) {
		sellPrice = price + (rate * price * 1.0 / 100.0);
		sellPrice = BigDecimal.valueOf(sellPrice).round(MathContext.UNLIMITED).setScale(2, RoundingMode.HALF_UP).doubleValue();
		return sellPrice;
	}
	
	public double markup() {
		return markup(sellPrice);
	}
	
	public double markup(double retail) {
		markup = ((retail - price) / price) * 100.0;
		markup = BigDecimal.valueOf(markup).round(MathContext.UNLIMITED).setScale(2, RoundingMode.HALF_UP).doubleValue();
		return markup;
	}
}
