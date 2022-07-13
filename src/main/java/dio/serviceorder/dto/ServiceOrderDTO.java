package dio.serviceorder.dto;

import dio.serviceorder.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderDTO {
    private Long id;

    @NotNull
    private CustomerDTO customer;

    @NotNull
    private Boolean closed;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ServiceType type;
}
