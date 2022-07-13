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
		SpringApplication.run(ServiceorderApplication.class, args);
	}

}

@Service
class RunnerToTest implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello spring");
		CustomerDTO customerDTO = new CustomerDTO(1L, "Jao");
		System.out.println("Customer DTO: " + customerDTO);
		CustomerMapper customerMapper = CustomerMapper.INSTANCE;
		Customer customer = customerMapper.toModel(customerDTO);
		System.out.println("Converted Customer: " + customer);

		ServiceOrderDTO serviceOrderDTO = new ServiceOrderDTO(1L, customerDTO, true, ServiceType.INSTALLATION);
		System.out.println("Service DTO :" + serviceOrderDTO);
		ServiceOrderMapper serviceOrderMapper = ServiceOrderMapper.INSTANCE;
		ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);
		System.out.println("Converted Service: " + serviceOrder);

		System.out.println("Json Customer DTO: " + JsonConvertionUtils.asJsonString(customerDTO));
		System.out.println("Json Service DTO: " + JsonConvertionUtils.asJsonString(serviceOrderDTO));

	}
}

class JsonConvertionUtils {
	public static String asJsonString(Object bookDTO) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			objectMapper.registerModules(new JavaTimeModule());

			return objectMapper.writeValueAsString(bookDTO);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
