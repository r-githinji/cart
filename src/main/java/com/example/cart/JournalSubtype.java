package com.example.cart;

public enum JournalSubtype {
	
	INVENTORY("Inventory", "jnl.stype.INV"), LOAN("Loan", "jnl.stype.LOA"), DEPOSIT("Deposit", "jnl.stype.DEP");
	private final String label, value;

	private JournalSubtype(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}
	public String getValue() {
		return value;
	}
}
