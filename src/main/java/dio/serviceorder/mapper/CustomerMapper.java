package dio.serviceorder.mapper;

import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer toModel(CustomerDTO customerDTO);

    CustomerDTO toDTO(Customer customer);
}
