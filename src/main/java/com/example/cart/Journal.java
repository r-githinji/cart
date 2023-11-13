package com.example.cart;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.function.Function;

import java.math.MathContext;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Transient;
import jakarta.persistence.Cacheable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Cacheable(false)
@Entity
@Table(name = "jnl")
public class Journal implements Serializable {
    
	private static final long serialVersionUID = 74098274641L;
	private Integer id;
	private LocalDateTime created;   
	private List<Entry> entries;
	private JournalType type;
	private String document;

	public Journal() {
	}

	public Journal(JournalType type, String document) {
		this.type = type;
		this.document = document;
		created = LocalDateTime.now();
	}
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "jnl_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotNull
	@Column(name = "jnl_type", nullable = false, updatable = false)
	public JournalType getType() {
		return type;
	}
	public void setType(JournalType type) {
		this.type = type;
	}
	@NotNull
	@Column(name = "jnl_created", updatable = false, nullable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@JsonManagedReference
	@OneToMany(mappedBy = "journal", cascade = {CascadeType.ALL})
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	@NotEmpty
	@Column(name = "jnl_doc", nullable = false, updatable = false)
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	@Transient
	public Double getDebits() {
		if (null == entries) {
			return 0.0;
		}
		return entries.stream().filter(e -> e.getType() == EntryType.DEBIT).map(Entry::getAmount).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
	}
	@Transient
	public Double getCredits() {
		if (null == entries) {
			return 0.0;
		}
		return entries.stream().filter(e -> e.getType() == EntryType.CREDIT).map(Entry::getAmount).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
	}
	
	public void put(Entry entry) {
		if (null == entries) {
			entries = new LinkedList<>();
		}
		entry.setJournal(this);
		entries.stream().filter(entry::equals).findFirst().ifPresent(entries::remove);
		entries.add(entry);
		reorder();
	}

	public void remove(Entry entry) {
		if (null == entries) {
			return;
		}
		entries.stream().filter(entry::equals).findFirst().ifPresent(entries::remove);
		reorder();
	}
    
	public void clear() {
		if (null == entries) {
			return;
		}
		entries.clear();
	}
	
	public void reorder() {
		if (null != entries) {
			IntStream.range(0, entries.size()).forEach(index -> entries.get(index).setSequence(index + 1));
		}
	}

	@Transient
	public boolean isBalanced() {
		return entries.stream().map(e -> {
			switch (e.getType()) {
				case DEBIT :
					return BigDecimal.valueOf(e.getAmount()).round(MathContext.UNLIMITED);
				case CREDIT : 
					return BigDecimal.valueOf(e.getAmount()).round(MathContext.UNLIMITED).negate();
				default : 
					return BigDecimal.ZERO;
			}       
		}).reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(BigDecimal.ZERO) == 0;
	} 
    
	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Journal)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Journal that = (Journal)other;
		return type == that.getType() && document.contentEquals(that.getDocument());
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, document);
	}
}
