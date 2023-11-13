package com.example.cart;

public enum SaleType {
    
	CASH("Cash", "CASH"), CREDIT("Credit", "CREDIT");
	private final String label, value;

	private SaleType(String label, String value) {
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
