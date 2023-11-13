package com.example.cart;

public enum AccountType {
    
	ASSET("Asset", "ASSET"), EXPENSE("Expense", "EXPENSE"), LIABILITY("Liability", "LIABILITY"), 
	EQUITY("Equity", "EQUITY"), REVENUE("Revenue", "REVENUE"), LOSS("Loss", "LOSS"), GAIN("Gain", "GAIN");
	private final String label, value;

	private AccountType(String label, String value) {
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
