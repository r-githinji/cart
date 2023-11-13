package com.example.cart;

public enum GenderType {
    
    	MALE("Male", "M"), 
	FEMALE("Female", "F");
    
	private final String label, value;

	private GenderType(String label, String value) {
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
