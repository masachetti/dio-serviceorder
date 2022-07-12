package dio.serviceorder.mapper;

import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.model.ServiceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceOrderMapper {
    ServiceOrderMapper INSTANCE = Mappers.getMapper(ServiceOrderMapper.class);

    ServiceOrder toModel(ServiceOrderDTO serviceOrderDTO);

    ServiceOrderDTO toDTO(ServiceOrder serviceOrder);
}
