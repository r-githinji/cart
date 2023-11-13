package com.example.cart;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Cacheable(false)
@Entity
@Table(name = "entry")
public class Entry implements Serializable {

	private static final long serialVersionUID = 692302378L;
	private Integer id, sequence;
	private Account account;
	private Double amount = 0.0, balance = 0.0;
	private Journal journal;
	private LocalDateTime created;
	private EntryType type;   

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "entry_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotNull
	@Positive
	@Column(name = "entry_seq", nullable = false, updatable = false)
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	@NotNull
	@ManyToOne
	@JoinColumn(name = "entry_acc")
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	@Column(name = "entry_amt", updatable = false, nullable = false)
	public Double getAmount() {       
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	@Transient
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	@JsonBackReference
	@NotNull
	@ManyToOne
	@JoinColumn(name = "entry_jnl")
	public Journal getJournal() {
		return journal;
	}
	public void setJournal(Journal journal) {
		this.journal = journal;
	}
	@NotNull
	@Column(name = "entry_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@NotNull
	@Column(name = "entry_type", updatable = false, nullable = false)
	public EntryType getType() {
		return type;
	}
	public void setType(EntryType type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, account);
	}	

	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Entry)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Entry that = (Entry)other;
		return type == that.getType() && account.equals(that.getAccount());
	}

	public static synchronized Entry of(EntryType type, Account acc, Double amt) {
		Entry entry = new Entry();
		entry.setType(type);
		entry.setAccount(acc);
		entry.setAmount(amt);
		entry.setCreated(LocalDateTime.now());
		return entry;
	}
}
