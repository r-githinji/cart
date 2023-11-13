package com.example.cart;

public enum SaleStatus {
    
	NEW("Quote", "NEW", "bg-green-500"), 
	ORDER("Sales Order", "ORDER", "bg-green-500"),
	INVOICE("Invoice", "INVOICE", "bg-green-500"),
	SHIPPED("In Transit", "SHIPPED", ""),
	DELIVERED("Delivered", "DELIVERED", ""), 
	CANCELLED("Cancelled", "CANCELLED", "bg-red-500"),
	PAID("Paid", "PAID", "bg-orange-500"), 
	RETURN("Return", "RETURN", "bg-red-500"),;
	private final String label, value, style;

	private SaleStatus(String label, String value, String style) {
		this.label = label;
		this.value = value;
		this.style = style;
	}

	public String getLabel() {
		return label;
	}
	public String getValue() {
		return value;
	}
	public String getStyle() {
		return style;
	}
}
