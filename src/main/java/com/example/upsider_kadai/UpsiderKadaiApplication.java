package com.example.upsider_kadai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.upsider_kadai.aggregates.invoice.InvoiceProperties;

@SpringBootApplication
@EnableConfigurationProperties({ InvoiceProperties.class })
public class UpsiderKadaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpsiderKadaiApplication.class, args);
	}

}
