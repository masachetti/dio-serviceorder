package dio.serviceorder.builder;


import dio.serviceorder.dto.CustomerDTO;
import lombok.Builder;

@Builder
public class CustomerDTOBuilder {
    @Builder.Default
    private long id = 1L;

    @Builder.Default
    private String name = "Alfredo";

    public CustomerDTO toCustomerDTO(){
        return new CustomerDTO(
                id,
                name
        );
    }
}
