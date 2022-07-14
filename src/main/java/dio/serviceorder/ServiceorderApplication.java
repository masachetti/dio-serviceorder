package dio.serviceorder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.enums.ServiceType;
import dio.serviceorder.mapper.CustomerMapper;
import dio.serviceorder.mapper.ServiceOrderMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.model.ServiceOrder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class ServiceorderApplication {

	public static void main(String[] args) {
		SpringApplication.	run(ServiceorderApplication.class, args);
	}

}
