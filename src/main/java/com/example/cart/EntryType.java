package com.example.cart;

public enum EntryType {
    
	DEBIT("Debit", "DR"), CREDIT("Credit", "CR");
	private final String label, value;

	private EntryType(String label, String value) {
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
