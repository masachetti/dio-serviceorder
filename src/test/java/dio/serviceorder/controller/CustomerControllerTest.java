package dio.serviceorder.controller;


import dio.serviceorder.builder.CustomerDTOBuilder;
import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.exception.CustomerAlreadyExistsException;
import dio.serviceorder.exception.CustomerNotFoundException;
import dio.serviceorder.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static dio.serviceorder.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private static final String API_URL_PATH = "/api/v1/customers";

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenACustomerIsCreated() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.createCustomer(customerDTO)).thenReturn(customerDTO);

        // then
        mockMvc.perform(post(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(customerDTO.getName())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();
        customerDTO.setName(null);

        // then
        mockMvc.perform(post(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPOSTIsCalledWithAnAlreadyCreatedIdThenAnErrorIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.createCustomer(customerDTO)).thenThrow(CustomerAlreadyExistsException.class);

        // then
        mockMvc.perform(post(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidIdThenOkStatusIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.findById(customerDTO.getId())).thenReturn(customerDTO);

        // then
        mockMvc.perform(get(API_URL_PATH + "/" + customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customerDTO.getName())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredIdThenNotFoundStatusIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.findById(customerDTO.getId())).thenThrow(CustomerNotFoundException.class);

        // then
        mockMvc.perform(get(API_URL_PATH + "/" + customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithCustomersIsCalledThenTheListOfCustomersIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.listAll()).thenReturn(Collections.singletonList(customerDTO));

        // then
        mockMvc.perform(get(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(customerDTO.getName())));
    }

    @Test
    void whenGETListWithoutCustomerIsCalledThenOkStatusIsReturned() throws Exception {
        // when
        when(customerService.listAll()).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        doNothing().when(customerService).deleteById(customerDTO.getId());

        // then
        mockMvc.perform(delete(API_URL_PATH + "/" + customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        doThrow(CustomerNotFoundException.class).when(customerService).deleteById(customerDTO.getId());

        // then
        mockMvc.perform(delete(API_URL_PATH + "/" + customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledWithAValidCustomerThenOkStatusIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.update(customerDTO)).thenReturn(customerDTO);

        // then
        mockMvc.perform(patch(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customerDTO.getName())));
    }

    @Test
    void whenPATCHIsCalledWithAnInvalidCustomerThenNotFoundStatusIsReturned() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTOBuilder.builder().build().toCustomerDTO();

        // when
        when(customerService.update(customerDTO)).thenThrow(CustomerNotFoundException.class);

        // then
        mockMvc.perform(patch(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isNotFound());
    }
}
