package com.example.cart;

//https://www.iedunote.com/types-of-accounting-journal
//https://accountinguide.com/loan-received-from-bank-journal-entry/
public enum JournalType {
    
	SALE("Sale", "jnl.SLE"), SALE_RETURN("Sales Return", "jnl.SRET"), 
	PURCHASE("Purchase", "jnl.PUR"), PURCHASE_RETURN("Purchase Return", "jnl.PRET"), 
	PAYMENT("Payment", "jnl.PAY"), RECEIPT("Receipt", "jnl.REC"), GENERAL("General", "jnl.GEN");    
	private final String label, value;

	private JournalType(String label, String value) {
		this.label = label;
		this.value= value;
	}

	public String getLabel() {
		return label;
	}
	public String getValue() {
		return value;
	}
}
