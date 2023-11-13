package com.example.cart;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@SpringBootApplication
public class CartApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartApplication.class, args);
	}

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(3);
		threadPool.setMaxPoolSize(15);
		threadPool.setWaitForTasksToCompleteOnShutdown(true);
		threadPool.initialize();
		return threadPool;
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonMapper() {
		return builder -> {
			builder.simpleDateFormat(Constants.SIMPLE_DATE_TIME_FORMAT);
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constants.SIMPLE_DATE_FORMAT),
					dateTimeFormat = DateTimeFormatter.ofPattern(Constants.SIMPLE_DATE_TIME_FORMAT);			
			builder.serializers(new LocalDateSerializer(dateFormat),
					new LocalDateTimeSerializer(dateTimeFormat));			
			builder.deserializers(new LocalDateDeserializer(dateFormat), 
					new LocalDateTimeDeserializer(dateTimeFormat));
		};
	}
}
