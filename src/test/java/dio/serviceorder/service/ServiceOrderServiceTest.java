package dio.serviceorder.service;

import dio.serviceorder.builder.CustomerDTOBuilder;
import dio.serviceorder.builder.ServiceOrderDTOBuilder;
import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.enums.ServiceType;
import dio.serviceorder.exception.ServiceOrderAlreadyExistsException;
import dio.serviceorder.exception.ServiceOrderNotFoundException;
import dio.serviceorder.mapper.CustomerMapper;
import dio.serviceorder.mapper.ServiceOrderMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.model.ServiceOrder;
import dio.serviceorder.repository.ServiceOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceOrderServiceTest {
    /*
    Funcionalidades desejadas:
        - Adicionar nova ordem de serviço
        - Buscar uma ordem de serviço
        - Buscar todas as ordem de serviços
        - Buscar apenas as ordens abertas
        - Buscasr apenas as orndens fechadas
        - Buscar todas as ordens de um cliente
        - Buscar por tipo de serviço
        - Alterar ordem de serviço
        - Deletar ordem de serviço
    */

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    private ServiceOrderMapper serviceOrderMapper = ServiceOrderMapper.INSTANCE;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    @Test
    void whenServiceOrderInformedThenItShouldBeCreated() throws ServiceOrderAlreadyExistsException {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findById(serviceOrderDTO.getId())).thenReturn(Optional.empty());
        when(serviceOrderRepository.save(serviceOrder)).thenReturn(serviceOrder);

        // then
        ServiceOrderDTO savedServiceOrderDTO = serviceOrderService.create(serviceOrderDTO);

        assertThat(savedServiceOrderDTO.getId(), is(equalTo(serviceOrderDTO.getId())));
        assertThat(savedServiceOrderDTO.getCustomer(), is(equalTo(serviceOrderDTO.getCustomer())));
        assertThat(savedServiceOrderDTO.getType(), is(equalTo(serviceOrderDTO.getType())));
        assertThat(savedServiceOrderDTO.getClosed(), is(equalTo(serviceOrderDTO.getClosed())));
    }

    @Test
    void whenServiceOrderWithInvalidIdInformedThenAnExceptionShouldBeThrown() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findById(serviceOrderDTO.getId())).thenReturn(Optional.of(serviceOrder));

        // then
        assertThrows(ServiceOrderAlreadyExistsException.class, ()-> serviceOrderService.create(serviceOrderDTO));
    }

    @Test
    void whenServiceOrderWithoutIdIsInformedThenItShouldBeCreated() throws ServiceOrderAlreadyExistsException {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        serviceOrderDTO.setId(null);
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.save(serviceOrder)).thenReturn(serviceOrder);

        // then
        ServiceOrderDTO savedServiceOrderDTO = serviceOrderService.create(serviceOrderDTO);

        assertThat(savedServiceOrderDTO.getCustomer(), is(equalTo(serviceOrderDTO.getCustomer())));
        assertThat(savedServiceOrderDTO.getType(), is(equalTo(serviceOrderDTO.getType())));
        assertThat(savedServiceOrderDTO.getClosed(), is(equalTo(serviceOrderDTO.getClosed())));
    }

    @Test
    void whenListServiceOrdersIsCalledThenReturnAListOfServiceOrders() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findAll()).thenReturn(Collections.singletonList(serviceOrder));

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAll();

        assertThat(foundListOfServiceOrdersDTO.get(0), is(equalTo(serviceOrderDTO)));
        assertThat(foundListOfServiceOrdersDTO, is(not(empty())));
    }

    @Test
    void whenListServiceOrderIsCalledThenReturnAnEmptyList() {
        // when
        when(serviceOrderRepository.findAll()).thenReturn(Collections.emptyList());

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAll();

        assertThat(foundListOfServiceOrdersDTO, is(empty()));
    }

    @Test
    void whenFindByIdIsCalledWithAValidIdThenReturnTheServiceOrder() throws ServiceOrderNotFoundException {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findById(serviceOrderDTO.getId())).thenReturn(Optional.of(serviceOrder));

        // then
        ServiceOrderDTO foundServiceOrderDTO = serviceOrderService.findById(serviceOrderDTO.getId());

        assertThat(foundServiceOrderDTO.getId(), is(equalTo(serviceOrderDTO.getId())));
        assertThat(foundServiceOrderDTO, is(equalTo(serviceOrderDTO)));
    }

    @Test
    void whenFindByIdIsCalledWithAnInvalidIdThenAnExceptionShouldBeThrown() {
        // when
        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ServiceOrderNotFoundException.class, ()-> serviceOrderService.findById(1L));
    }

    @Test
    void whenListByClosedStatusIsCalledThenAListOfServiceOrderShouldBeReturned() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findByClosed(serviceOrderDTO.getClosed()))
                .thenReturn(Collections.singletonList(serviceOrder));

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAllWithClosedStatus(serviceOrderDTO.getClosed());

        assertThat(foundListOfServiceOrdersDTO.get(0), is(equalTo(serviceOrderDTO)));
        assertThat(foundListOfServiceOrdersDTO, is(not(empty())));
    }

    @Test
    void whenListByClosedStatusIsCalledThenAEmptyListShouldBeReturned() {
        // given
        Boolean testClosed = true;
        // when
        when(serviceOrderRepository.findByClosed(testClosed)).thenReturn(Collections.emptyList());

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAllWithClosedStatus(testClosed);

        assertThat(foundListOfServiceOrdersDTO, is(empty()));
    }

    @Test
    void whenListByCustomerIsCalledThenAListOfServiceOrderShouldBeReturned() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findByCustomer(serviceOrder.getCustomer()))
                .thenReturn(Collections.singletonList(serviceOrder));

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAllOfCustomer(serviceOrder.getCustomer());

        assertThat(foundListOfServiceOrdersDTO.get(0), is(equalTo(serviceOrderDTO)));
        assertThat(foundListOfServiceOrdersDTO, is(not(empty())));
    }

    @Test
    void whenListByCustomerIsCalledThenAEmptyListShouldBeReturned() {
        // given
        Customer testCustomer = CustomerMapper.INSTANCE.toModel(
                CustomerDTOBuilder.builder().build().toCustomerDTO()
        );
        // when
        when(serviceOrderRepository.findByCustomer(testCustomer)).thenReturn(Collections.emptyList());

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAllOfCustomer(testCustomer);

        assertThat(foundListOfServiceOrdersDTO, is(empty()));
    }

    @Test
    void whenListByServiceTypeIsCalledThenAListOfServiceOrderShouldBeReturned() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findByServiceType(serviceOrder.getType()))
                .thenReturn(Collections.singletonList(serviceOrder));

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAllWithServiceType(serviceOrder.getType());

        assertThat(foundListOfServiceOrdersDTO.get(0), is(equalTo(serviceOrderDTO)));
        assertThat(foundListOfServiceOrdersDTO, is(not(empty())));
    }

    @Test
    void whenListByServiceTypeIsCalledThenAEmptyListShouldBeReturned() {
        // given
        ServiceType testType = ServiceType.INSTALLATION;

        // when
        when(serviceOrderRepository.findByServiceType(testType)).thenReturn(Collections.emptyList());

        // then
        List<ServiceOrderDTO> foundListOfServiceOrdersDTO = serviceOrderService.listAllWithServiceType(testType);

        assertThat(foundListOfServiceOrdersDTO, is(empty()));
    }

    @Test
    void whenDeleteIsCalledWithAValidServiceOrderIdThenACustomerShouldBeDeleted() throws ServiceOrderNotFoundException {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findById(serviceOrderDTO.getId())).thenReturn(Optional.of(serviceOrder));
        doNothing().when(serviceOrderRepository).deleteById(serviceOrderDTO.getId());

        // then
        serviceOrderService.deleteById(serviceOrderDTO.getId());

        verify(serviceOrderRepository, times(1)).deleteById(serviceOrderDTO.getId());
    }

    @Test
    void whenDeleteIsCalledWithAnInvalidServiceOrderIdThenAnExceptionShouldBeThrown() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();


        // when
        when(serviceOrderRepository.findById(serviceOrderDTO.getId())).thenReturn(Optional.empty());


        // then
        assertThrows(ServiceOrderNotFoundException.class, () -> serviceOrderService.deleteById(serviceOrderDTO.getId()));
    }
    @Test
    void whenUpdateIsCalledWithAValidServiceOrderThenTheServiceOrderShouldBeUpdated() throws ServiceOrderNotFoundException {
        // given
        ServiceOrderDTO foundedServiceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrderDTO serviceOrderToUpdateDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        serviceOrderToUpdateDTO.setClosed(!foundedServiceOrderDTO.getClosed());
        serviceOrderToUpdateDTO.setType(ServiceType.REMOVAL);

        ServiceOrder foundedServiceOrder = serviceOrderMapper.toModel(foundedServiceOrderDTO);
        ServiceOrder serviceOrderToUpdate = serviceOrderMapper.toModel(serviceOrderToUpdateDTO);

        // when
        when(serviceOrderRepository.findById(foundedServiceOrderDTO.getId())).thenReturn(Optional.of(foundedServiceOrder));
        when(serviceOrderRepository.save(serviceOrderToUpdate)).thenReturn(serviceOrderToUpdate);

        // then
        ServiceOrderDTO updatedServiceOrderDTO = serviceOrderService.update(serviceOrderToUpdateDTO);

        assertThat(updatedServiceOrderDTO, equalTo(serviceOrderToUpdateDTO));
    }

    @Test
    void whenUpdateIsCalledWithAnInvalidServiceOrderIdThenAnExceptionShouldBeThrown() {
        // given
        ServiceOrderDTO serviceOrderDTO = ServiceOrderDTOBuilder.builder().build().toServiceOrderDTO();
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);

        // when
        when(serviceOrderRepository.findById(serviceOrderDTO.getId())).thenReturn(Optional.empty());

        // then
        assertThrows(ServiceOrderNotFoundException.class, () -> serviceOrderService.update(serviceOrderDTO));
        verify(serviceOrderRepository, times(0)).save(serviceOrder);
    }

}
