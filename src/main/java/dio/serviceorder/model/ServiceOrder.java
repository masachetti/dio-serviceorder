package dio.serviceorder.model;


import dio.serviceorder.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tb_service_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Customer customer;

    @Column(nullable = false)
    private boolean closed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;
}
