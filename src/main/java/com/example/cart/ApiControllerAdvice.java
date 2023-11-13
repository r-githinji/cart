package com.example.cart;

import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

@RestControllerAdvice
public class ApiControllerAdvice {

	@Autowired
	private ResourceLoader loader;
	
	@ExceptionHandler(value = {Exceptions.ImageNotFoundException.class})
	public @ResponseBody byte[] handleImageNotFoundException(Exceptions.ImageNotFoundException ex, WebRequest request) throws IOException {
		Resource resource = loader.getResource("classpath:images/blank.png");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
	    	try (InputStream is = resource.getInputStream()) {
			BufferedImage image = ImageIO.read(is);		
			ImageIO.write(image, "png", baos);
		}
		return baos.toByteArray();
	}
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<List<Growl>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
		List<Growl> growls = ex.getAllErrors().stream()
			.map(error -> Growl.of(error.getCode(), "error", error.getDefaultMessage())).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(growls);
	}
	
	@ExceptionHandler(value = {Exceptions.ApplicationException.class})
	public ResponseEntity<Growl> handleApplicationException(Exceptions.ApplicationException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getGrowl());
	}
}

