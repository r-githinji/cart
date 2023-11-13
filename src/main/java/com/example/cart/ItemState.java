package com.example.cart;

public enum ItemState {
    
	NEW("New", "NEW"), USED("Used", "USED");
	private final String label, value;

	private ItemState(String label, String value) {
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
