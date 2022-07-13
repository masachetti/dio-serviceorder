package dio.serviceorder.service;

import dio.serviceorder.builder.ServiceOrderDTOBuilder;
import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.exception.CustomerAlreadyExistsException;
import dio.serviceorder.exception.ServiceOrderAlreadyExistsException;
import dio.serviceorder.mapper.ServiceOrderMapper;
import dio.serviceorder.model.ServiceOrder;
import dio.serviceorder.repository.ServiceOrderRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
