package dio.serviceorder.controller;

import dio.serviceorder.builder.ServiceOrderDTOBuilder;
import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.exception.ServiceOrderAlreadyExistsException;
import dio.serviceorder.exception.ServiceOrderNotFoundException;
import dio.serviceorder.service.ServiceOrderService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ServiceOrderControllerTest {

    private static final String API_URL_PATH = "/api/v1/service-order";

    private MockMvc mockMvc;

    @Mock
    private ServiceOrderService serviceOrderService;

    @InjectMocks
    private ServiceOrderController serviceOrderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(serviceOrderController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    /*          POST            */
    @Test
    void whenPOSTIsCalledThenAServiceOrderIsCreated() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        // when
        when(serviceOrderService.create(serviceOrderDTO)).thenReturn(serviceOrderDTO);

        // then
        mockMvc.perform(post(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceOrderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.closed", is(serviceOrderDTO.getClosed())))
                .andExpect(jsonPath("$.type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$.customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        serviceOrderDTO.setType(null);

        // then
        mockMvc.perform(post(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceOrderDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPOSTIsCalledWithAnAlreadyCreatedIdThenAnErrorIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();


        // when
        when(serviceOrderService.create(serviceOrderDTO)).thenThrow(ServiceOrderAlreadyExistsException.class);

        // then
        mockMvc.perform(post(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceOrderDTO)))
                .andExpect(status().isBadRequest());
    }

    /*          GET - /id            */

    @Test
    void whenGETIsCalledWithValidIdThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();


        // when
        when(serviceOrderService.findById(serviceOrderDTO.getId())).thenReturn(serviceOrderDTO);

        // then
        mockMvc.perform(get(API_URL_PATH + "/" + serviceOrderDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.closed", is(serviceOrderDTO.getClosed())))
                .andExpect(jsonPath("$.type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$.customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredIdThenNotFoundStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.findById(serviceOrderDTO.getId())).thenThrow(ServiceOrderNotFoundException.class);

        // then
        mockMvc.perform(get(API_URL_PATH + "/" + serviceOrderDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /*          GET - /open            */
    @Test
    void whenGETListOfOpenServiceOrderIsCalledWithServiceOrdersThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        serviceOrderDTO.setClosed(false);

        // when
        when(serviceOrderService.listAllWithClosedStatus(false)).thenReturn(Collections.singletonList(serviceOrderDTO));

        // then
        mockMvc.perform(get(API_URL_PATH + "/open")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].closed", is(serviceOrderDTO.getClosed())))
                .andExpect(jsonPath("$[0].type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$[0].customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenGETListOfOpenServiceOrderIsCalledWithoutServiceOrdersThenOkStatusIsReturned() throws Exception {
        // when
        when(serviceOrderService.listAllWithClosedStatus(false)).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(API_URL_PATH + "/open")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    /*          GET - /closed            */
    @Test
    void whenGETListOfClosedServiceOrderIsCalledWithServiceOrdersThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        serviceOrderDTO.setClosed(true);

        // when
        when(serviceOrderService.listAllWithClosedStatus(true)).thenReturn(Collections.singletonList(serviceOrderDTO));

        // then
        mockMvc.perform(get(API_URL_PATH + "/closed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].closed", is(serviceOrderDTO.getClosed())))
                .andExpect(jsonPath("$[0].type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$[0].customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenGETListOfClosedServiceOrderIsCalledWithoutServiceOrdersThenOkStatusIsReturned() throws Exception {
        // when
        when(serviceOrderService.listAllWithClosedStatus(true)).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(API_URL_PATH + "/closed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    /*          GET - /customer            */
    @Test
    void whenGETListOfCustomerServiceOrdersIsCalledWithServiceOrdersThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.listAllOfCustomer(serviceOrderDTO.getCustomer())).thenReturn(Collections.singletonList(serviceOrderDTO));

        // then
        mockMvc.perform(get(API_URL_PATH + "/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceOrderDTO.getCustomer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].closed", is(serviceOrderDTO.getClosed())))
                .andExpect(jsonPath("$[0].type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$[0].customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenGETListOfCustomerServiceOrdersIsCalledWithoutServiceOrdersThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        CustomerDTO customerDTO = serviceOrderDTO.getCustomer();

        // when
        when(serviceOrderService.listAllOfCustomer(customerDTO)).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(API_URL_PATH + "/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    /*          GET - /type            */
    @Test
    void whenGETListOfServiceOrdersWithAValidTypeIsCalledWithServiceOrdersThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.listAllWithServiceType(serviceOrderDTO.getType())).thenReturn(Collections.singletonList(serviceOrderDTO));

        // then
        mockMvc.perform(get(API_URL_PATH + "/type/" + serviceOrderDTO.getType().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].closed", is(serviceOrderDTO.getClosed())))
                .andExpect(jsonPath("$[0].type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$[0].customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenGETListOfServiceOrdersWithAValidTypeIsCalledWithoutServiceOrdersThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.listAllWithServiceType(serviceOrderDTO.getType())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(API_URL_PATH + "/type/" + serviceOrderDTO.getType().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }
    @Test
    void whenGETListOfServiceOrdersWithAInvalidTypeIsCalledThenABadRequestShouldBeReturned() throws Exception {
        // given
        String wrongServiceType = "WRONG_SERVICE_TYPE";

        // then
        mockMvc.perform(get(API_URL_PATH + "/type/" + wrongServiceType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /*          GET            */

    @Test
    void whenGETListWithServiceOrderIsCalledThenTheListOfServiceOrdersIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.listAll()).thenReturn(Collections.singletonList(serviceOrderDTO));

        // then
        mockMvc.perform(get(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$[0].customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenGETListWithoutServiceOrdersIsCalledThenOkStatusIsReturned() throws Exception {
        // when
        when(serviceOrderService.listAll()).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    /*          DELETE            */
    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        doNothing().when(serviceOrderService).deleteById(serviceOrderDTO.getId());

        // then
        mockMvc.perform(delete(API_URL_PATH + "/" + serviceOrderDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        doThrow(ServiceOrderNotFoundException.class).when(serviceOrderService).deleteById(serviceOrderDTO.getId());

        // then
        mockMvc.perform(delete(API_URL_PATH + "/" + serviceOrderDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /*          PATCH            */
    @Test
    void whenPATCHIsCalledWithAValidServiceOrderThenOkStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.update(serviceOrderDTO)).thenReturn(serviceOrderDTO);

        // then
        mockMvc.perform(patch(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is(serviceOrderDTO.getType().toString())))
                .andExpect(jsonPath("$.customer.name", is(serviceOrderDTO.getCustomer().getName())));
    }

    @Test
    void whenPATCHIsCalledWithAnInvalidCustomerThenNotFoundStatusIsReturned() throws Exception {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();

        // when
        when(serviceOrderService.update(serviceOrderDTO)).thenThrow(ServiceOrderNotFoundException.class);

        // then
        mockMvc.perform(patch(API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceOrderDTO)))
                .andExpect(status().isNotFound());
    }

}
