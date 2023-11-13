package com.example.cart;

import java.io.IOException;

public abstract class Exceptions {		
	
	public static class ApplicationException extends RuntimeException {

		private final static long serialVersionUID = 273828L;
		private Growl growl;
		
		public ApplicationException(Growl growl) {
			this.growl = growl;
		}
		public Growl getGrowl() {
			return growl;
		}
	}
	
	public static class ImageNotFoundException extends RuntimeException {
	
		private static final long serialVersionUID = 7817381L;
		
		public ImageNotFoundException(IOException ioex) {
			super(ioex);
		}
	}
}
