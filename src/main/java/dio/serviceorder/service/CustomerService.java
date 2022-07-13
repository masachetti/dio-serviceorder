package dio.serviceorder.service;

import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.exception.CustomerAlreadyExistsException;
import dio.serviceorder.exception.CustomerNotFoundException;
import dio.serviceorder.mapper.CustomerMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws CustomerAlreadyExistsException {
        Customer customer = customerMapper.toModel(customerDTO);
        if (customerDTO.getId() != null && checkIfIdExists(customer.getId()))
            throw new CustomerAlreadyExistsException(customer.getId());
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    public List<CustomerDTO> listAll(){
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO findById(Long id) throws CustomerNotFoundException {
        Customer foundCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return customerMapper.toDTO(foundCustomer);
    }

    public void deleteById(Long id) throws CustomerNotFoundException {
        if (!checkIfIdExists(id))
            throw new CustomerNotFoundException(id);
        customerRepository.deleteById(id);
    }

    public CustomerDTO update(CustomerDTO customerDTO) throws CustomerNotFoundException {
        if (!checkIfIdExists(customerDTO.getId()))
            throw new CustomerNotFoundException(customerDTO.getId());
        Customer customer = customerMapper.toModel(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    private boolean checkIfIdExists(Long id){
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional.isPresent();
    }
}
