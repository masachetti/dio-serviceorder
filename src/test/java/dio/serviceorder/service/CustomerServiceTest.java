package dio.serviceorder.service;

import dio.serviceorder.builder.CustomerDTOBuilder;
import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.exception.CustomerAlreadyCreatedException;
import dio.serviceorder.exception.CustomerNotFoundException;
import dio.serviceorder.mapper.CustomerMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerMapper customerMapper = CustomerMapper.INSTANCE;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void whenCustomerInformedThenItShouldBeCreated() throws CustomerAlreadyCreatedException {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.empty());
        when(customerRepository.save(customer)).thenReturn(customer);

        // then
        CustomerDTO createdCustomerDTO = customerService.createCustomer(customerDTO);

        assertThat(createdCustomerDTO.getId(), is(equalTo(customerDTO.getId())));
        assertThat(createdCustomerDTO.getName(), is(equalTo(customerDTO.getName())));
    }

    @Test
    void whenCustomerWithAlreadyUsedIdIsInformedThenAnExceptionShouldBeThrown() {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.of(customer));

        // then
        assertThrows(CustomerAlreadyCreatedException.class, ()-> customerService.createCustomer(customerDTO));
    }

    @Test
    void whenCustomerWithoutIdIsInformedThenItShouldBeCreated() throws CustomerAlreadyCreatedException {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        customerDTO.setId(null);
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.save(customer)).thenReturn(customer);

        // then
        CustomerDTO createdCustomerDTO = customerService.createCustomer(customerDTO);

        assertThat(createdCustomerDTO.getId(), is(equalTo(customerDTO.getId())));
        assertThat(createdCustomerDTO.getName(), is(equalTo(customerDTO.getName())));
        verify(customerRepository, times(0)).findById(customerDTO.getId());
    }

    @Test
    void whenListCustomerIsCalledThenReturnAListOfCustomer() {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        // then
        List<CustomerDTO> foundListCustomersDTO = customerService.listAll();

        assertThat(foundListCustomersDTO.get(0), is(equalTo(customerDTO)));
        assertThat(foundListCustomersDTO, is(not(empty())));
    }

    @Test
    void whenListCustomerIsCalledThenReturnAnEmptyList() {
        // when
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // then
        List<CustomerDTO> foundListCustomersDTO = customerService.listAll();

        assertThat(foundListCustomersDTO, is(empty()));
    }

    @Test
    void whenFindByIdIsCalledWithAValidCustomerIdThenReturnTheCustomer() throws CustomerNotFoundException {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.of(customer));

        // then
        CustomerDTO foundCustomerDTO = customerService.findById(customerDTO.getId());

        assertThat(foundCustomerDTO.getId(), is(equalTo(customerDTO.getId())));
    }

    @Test
    void whenFindByIdIsCalledWithANonexistentCustomerIdThenAnExceptionShouldBeThrown() {
        // when
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(CustomerNotFoundException.class, ()-> customerService.findById(1L));
    }

    @Test
    void whenDeleteIsCalledWithAValidCustomerIdThenACustomerShouldBeDeleted() throws CustomerNotFoundException {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customerDTO.getId());

        // then
        customerService.deleteById(customerDTO.getId());

        verify(customerRepository, times(1)).deleteById(customerDTO.getId());
    }

    @Test
    void whenDeleteIsCalledWithAnInvalidCustomerIdThenAnExceptionShouldBeThrown() {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.empty());

        // then
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteById(customerDTO.getId()));
    }

    @Test
    void whenUpdateIsCalledWithAValidCustomerThenTheCustomerShouldBeUpdated() throws CustomerNotFoundException {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        CustomerDTO customerToUpdateDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        customerToUpdateDTO.setName("Maria");
        Customer createdCustomer = customerMapper.toModel(customerDTO);
        Customer updatedCustomer = customerMapper.toModel(customerToUpdateDTO);

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.of(createdCustomer));
        when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);

        // then
        CustomerDTO updatedCustomerDTO = customerService.update(customerToUpdateDTO);

        assertThat(updatedCustomerDTO, equalTo(customerToUpdateDTO));
    }

    @Test
    void whenUpdateIsCalledWithAnInvalidCustomerIdThenAnExceptionShouldBeThrown() {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        Customer customer = customerMapper.toModel(customerDTO);

        // when
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.empty());

        // then
        assertThrows(CustomerNotFoundException.class, () -> customerService.update(customerDTO));
        verify(customerRepository, times(0)).save(customer);
    }
}
