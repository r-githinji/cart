package com.example.cart;

import java.io.Serializable;
import java.util.Objects;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Cacheable;
import jakarta.validation.constraints.NotNull;

@Cacheable(false)
@Entity
@Table(name = "acc")
@NamedNativeQuery(
	name = "accQuery", 		
	query = " "
		+ "	WITH cte AS ( "	
		+ "		SELECT a.account "	
		+ "			FROM (VALUES(?)) a (account) "	
		+ "		WHERE true "	
		+ "	), "
		+ "	cte0 AS ( "
		+ "		SELECT e.entry_acc,"
		+ "			SUM(CASE "
		+ "				WHEN e.entry_type = 'DR'"
		+ "					THEN "
		+ "						CASE "
		+ "							WHEN a.acc_type IN ('ASSET', 'EXPENSE') "
		+ "								THEN e.entry_amt "
		+ "							ELSE e.entry_amt * -1 "
		+ "						END"
		+ "				WHEN e.entry_type = 'CR'"
		+ "					THEN "
		+ "						CASE "
		+ "							WHEN a.acc_type IN ('ASSET', 'EXPENSE') "
		+ "								THEN e.entry_amt * -1 "
		+ "							ELSE e.entry_amt "
		+ "						END"
		+ "				ELSE 0 "
		+ "			END) AS acc_bal "
		+ "			FROM entry e JOIN acc a ON e.entry_acc = a.acc_no "
		+ "		WHERE true "
		+ "		GROUP BY e.entry_acc "
		+ "	)"
		+ "	SELECT a.acc_no, a.acc_created, a.acc_desc, a.acc_type, COALESCE(c.acc_bal, 0) AS acc_bal "
		+ "		FROM acc a LEFT JOIN cte0 c ON c.entry_acc = a.acc_no CROSS JOIN cte b "
		+ "	WHERE true "
		+ "	AND "
		+ "	CASE "
		+ "		WHEN b.account IS NULL "
		+ "			THEN true "
		+ "		WHEN b.account = a.acc_no "
		+ "			THEN true "
		+ "		ELSE false "
		+ "	END ",
	resultClass = Account.class
)
public class Account implements Serializable {
    
	private static final long serialVersionUID = 267223789349L;
	private String number, description;
	private Double balance = 0.0;
	private LocalDateTime created;
	private AccountType type;

	public Account() {
	}

	public Account(String number, String description) {
		this.number = number;
		this.description = description;
	}

	@Id
	@Column(name = "acc_no")
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	@NotNull
	@Column(name = "acc_desc", nullable = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Column(name = "acc_bal", insertable = false, updatable = false)
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	@NotNull
	@Column(name = "acc_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	@NotNull
	@Column(name = "acc_type", nullable = false, updatable = false)
	public AccountType getType() {
		return type;
	}
	public void setType(AccountType type) {
		this.type = type;
	}
    
	@Override
	public int hashCode() {
		return Objects.hash(number, type);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof Account)) {
			return false;
		} else if (this == other) {
			return true;
		}
		Account that = (Account)other;
		return number.equals(that.getNumber());
	}
}
