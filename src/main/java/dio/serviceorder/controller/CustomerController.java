package dio.serviceorder.controller;

import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.exception.CustomerAlreadyExistsException;
import dio.serviceorder.exception.CustomerNotFoundException;
import dio.serviceorder.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@RequestBody @Valid CustomerDTO customerDTO) throws CustomerAlreadyExistsException {
        return customerService.createCustomer(customerDTO);
    }

    @GetMapping("/{id}")
    public CustomerDTO findById(@PathVariable Long id) throws CustomerNotFoundException {
        return customerService.findById(id);
    }

    @GetMapping
    public List<CustomerDTO> listCustomers(){
        return customerService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws CustomerNotFoundException {
        customerService.deleteById(id);
    }

    @PatchMapping()
    public CustomerDTO updateCustomer(@RequestBody @Valid CustomerDTO customerDTO) throws CustomerNotFoundException {
        return customerService.update(customerDTO);
    }
}
