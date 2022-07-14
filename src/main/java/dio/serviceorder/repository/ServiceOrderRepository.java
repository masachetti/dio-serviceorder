package dio.serviceorder.repository;

import dio.serviceorder.enums.ServiceType;
import dio.serviceorder.model.Customer;
import dio.serviceorder.model.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    List<ServiceOrder> findByClosed(Boolean closed);
    List<ServiceOrder> findByCustomer(Customer customer);
    List<ServiceOrder> findByType(ServiceType type);
}
