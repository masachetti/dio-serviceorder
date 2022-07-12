package dio.serviceorder.service;

import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.exception.CustomerAlreadyCreated;
import dio.serviceorder.mapper.CustomerMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws CustomerAlreadyCreated {
        Customer customer = customerMapper.toModel(customerDTO);
        if (customer.getId() == null || !checkIfCustomerIdAlreadyBeUsed(customer)){
            Customer savedCustomer = customerRepository.save(customer);
            return customerMapper.toDTO(savedCustomer);
        }
        throw new CustomerAlreadyCreated(customer.getId());
    }

    private boolean checkIfCustomerIdAlreadyBeUsed(Customer customer){
        Optional<Customer> customerOptional = customerRepository.findById(customer.getId());
        return customerOptional.isPresent();
    }

}
