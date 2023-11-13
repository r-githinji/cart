package com.example.cart;

import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Cacheable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EntityResult;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Cacheable(false)
@Entity
@Table(name = "sle")
public class Sale implements Serializable {
    
	private static final long serialVersionUID = 7123783292L;
	private Integer id;
	private String reference;
	private LocalDateTime created;
	private Customer debtor;
	private Double gross = 0.0, nett = 0.0, tax = 0.0, shipping = 0.0;
	private LocalDateTime postedOn;
	private SaleStatus status;
	private SaleType type; 
	private Address shipTo;
	private boolean direct;
	private List<SaleLine> lines;

	public Sale() {    	
	}

	public Sale(SaleType type) {
		this(type, null);
	}

	public Sale(SaleType type, String reference) {
		this.reference = reference;
		this.type = type;    	
		status = SaleStatus.NEW;
		created = LocalDateTime.now();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sle_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotEmpty(message = "{sale.reference.isempty}")
	@Column(name = "sle_ref", nullable = false, updatable = false)
    	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	@NotNull
	@Column(name = "sle_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@NotNull
	@OneToOne
	@JoinColumn(name = "sle_debtor")
	public Customer getDebtor() {
		return debtor;
	}
	public void setDebtor(Customer debtor) {
		this.debtor = debtor;
	}
	@NotNull
	@Column(name = "sle_gross", updatable = false, nullable = false)
	public Double getGross() {
		return gross;
	}
	public void setGross(Double total) {
		this.gross = total;
	}
	@NotNull
	@Positive
	@Column(name = "sle_nett", updatable = false, nullable = false)
	public Double getNett() {
		return nett;
	}
	public void setNett(Double nett) {
		this.nett = nett;
	}
	@NotNull
	@PositiveOrZero
	@Column(name = "sle_shipping", updatable = false, nullable = false)
	public Double getShipping() {
		return shipping;
	}
	public void setShipping(Double shipping) {
		this.shipping = shipping;
	}
	@NotNull
	@Positive
	@Column(name = "sle_tax", updatable = false, nullable = false)
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	@NotNull
	@Column(name = "sle_postedon", nullable = false, updatable = false)
	public LocalDateTime getPostedOn() {
		return postedOn;
	}
	public void setPostedOn(LocalDateTime postedOn) {
		this.postedOn = postedOn;
	} 
	@NotNull
	@Column(name = "sle_status", nullable = false, updatable = false)
	public SaleStatus getStatus() {
		return status;
	}
	public void setStatus(SaleStatus state) {
		this.status = state;
	}  
	@NotNull
	@Column(name = "sle_type", nullable = false, updatable = false)
	public SaleType getType() {
		return type;
	}
	public void setType(SaleType type) {
		this.type = type;
	}	
	@Column(name = "sle_isdirect", updatable = false)
	public boolean isDirect() {
		return direct;
	}
	public void setDirect(boolean direct) {
		this.direct = direct;
	}
	@JsonManagedReference(value = "lines")
	@OneToMany(mappedBy = "sale", cascade = {CascadeType.ALL})
	public List<SaleLine> getLines() {
		return lines;
	}
	public void setLines(List<SaleLine> lines) {
		this.lines = lines;
	}
	@NotNull
	@OneToOne
	@JoinColumn(name = "sle_shipto")
	public Address getShipTo() {
		return shipTo;
	}
	public void setShipTo(Address shipTo) {
		this.shipTo = shipTo;
	}
	@Transient
	public long getAmountInCents() {		
		BigDecimal amount = BigDecimal.valueOf(getGross())//.setScale(2, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100));
		return amount.longValue();
	}
	
	public void put(SaleLine line) {
		if (null == line) {
			return;
		}
		if (null == lines) {
			lines = new LinkedList<>();
		}
		line.setSale(this);
		lines.stream().filter(line::equals).findFirst().ifPresent(lines::remove);
		lines.add(line);
	}
	
	public void remove(SaleLine line) {
		if (null== line || null == lines) {
			return;
		}
		lines.stream().filter(line::equals).findFirst().ifPresent(lines::remove);
	}
	
	public void clear() {
		if (null == lines) {
			return;			
		}
		lines.clear();
	}

	public final void aggregate() {
		gross = nett = tax = shipping = 0.0 ;
		if (null== lines) {
			return;
		}
		lines.forEach(SaleLine::aggregate);
		nett = lines.stream().map(SaleLine::getNett).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
		tax = lines.stream().map(SaleLine::getTax).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
		gross = nett + tax;        
	}
    
	@Override
	public int hashCode() {
		return Objects.hash(reference);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Sale)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Sale that = (Sale)other;
		return reference.equals(that.getReference());
	}
}
