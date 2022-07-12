package dio.serviceorder.builder;

import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.enums.ServiceType;
import lombok.Builder;

@Builder
public class ServiceOrderDTOBuilder {
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private CustomerDTO customer = CustomerDTOBuilder.builder().build().toCustomerDTO();

    @Builder.Default
    private boolean closed = false;

    @Builder.Default
    private ServiceType type = ServiceType.INSTALLATION;

    public ServiceOrderDTO toServiceOrderDTO() {
        return new ServiceOrderDTO(id,
                customer,
                closed,
                type);
    }
}
