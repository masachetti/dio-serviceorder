package dio.serviceorder.controller;

import dio.serviceorder.dto.CustomerDTO;
import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.enums.ServiceType;
import dio.serviceorder.exception.ServiceOrderAlreadyExistsException;
import dio.serviceorder.exception.ServiceOrderNotFoundException;
import dio.serviceorder.service.ServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/service-order")
public class ServiceOrderController {

    @Autowired
    private ServiceOrderService serviceOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceOrderDTO createServiceOrder(@RequestBody @Valid ServiceOrderDTO serviceOrderDTO) throws ServiceOrderAlreadyExistsException {
        return serviceOrderService.create(serviceOrderDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteServiceOrder(@PathVariable Long id) throws ServiceOrderNotFoundException {
        serviceOrderService.deleteById(id);
    }

    @PatchMapping()
    public ServiceOrderDTO updateServiceOrder(@RequestBody @Valid ServiceOrderDTO serviceOrderDTO) throws ServiceOrderNotFoundException {
        return serviceOrderService.update(serviceOrderDTO);
    }

    @GetMapping("/{id}")
    public ServiceOrderDTO findById(@PathVariable Long id) throws ServiceOrderNotFoundException {
        return serviceOrderService.findById(id);
    }

    @GetMapping()
    public List<ServiceOrderDTO> listServiceOrders(){
        return serviceOrderService.listAll();
    }

    @GetMapping("/open")
    public List<ServiceOrderDTO> listOpenServiceOrders(){
        return serviceOrderService.listAllWithClosedStatus(false);
    }

    @GetMapping("/closed")
    public List<ServiceOrderDTO> listClosedServiceOrders(){
        return serviceOrderService.listAllWithClosedStatus(true);
    }

    @GetMapping("/customer")
    public List<ServiceOrderDTO> listCustomerServiceOrders(@RequestBody @Valid CustomerDTO customerDTO){
        return serviceOrderService.listAllOfCustomer(customerDTO);
    }

    @GetMapping("/type/{serviceType}")
    public List<ServiceOrderDTO> listServiceOrdersByType(@PathVariable @Valid ServiceType serviceType){
        return serviceOrderService.listAllWithServiceType(serviceType);
    }
}
