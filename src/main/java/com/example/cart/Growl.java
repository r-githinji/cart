package com.example.cart;

public class Growl {

	private String target, severity, detail;

	public Growl() {
		this(null, null);
	}
	
	public Growl(String severity, String detail) {
		this(null, severity, detail);
	}

	public Growl(String target, String severity, String detail) {
		this.target = target;
		this.severity = severity;
		this.detail = detail;
	}

	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public static synchronized Growl of(String severity, String detail) {
		return new Growl(severity, detail);
	}
	
	public static synchronized Growl of(String target, String severity, String detail) {
		return new Growl(target, severity, detail);
	}
}
