package dio.serviceorder.service;

import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.exception.CustomerAlreadyExistsException;
import dio.serviceorder.exception.ServiceOrderAlreadyExistsException;
import dio.serviceorder.mapper.ServiceOrderMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.model.ServiceOrder;
import dio.serviceorder.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceOrderService {
    @Autowired
    ServiceOrderRepository serviceOrderRepository;

    ServiceOrderMapper serviceOrderMapper = ServiceOrderMapper.INSTANCE;

    public ServiceOrderDTO create(ServiceOrderDTO serviceOrderDTO) throws ServiceOrderAlreadyExistsException {
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);
        if (serviceOrderDTO.getId() != null && checkIfIdExists(serviceOrderDTO.getId()))
            throw new ServiceOrderAlreadyExistsException(serviceOrderDTO.getId());
        ServiceOrder savedServiceOrder = serviceOrderRepository.save(serviceOrder);
        return serviceOrderMapper.toDTO(savedServiceOrder);
    }

    public void deleteById(Long id){
    }

    public ServiceOrderDTO update(Long id, ServiceOrderDTO serviceOrderDTO){
        return serviceOrderDTO;
    }

//    public ServiceOrderDTO findById(Long id){
//
//    }
//
//    public List<ServiceOrderDTO> findAll(){
//
//    }
//
//    public List<ServiceOrderDTO> findByClosedStatus(boolean closedStatus){
//
//    }
//
//    public List<ServiceOrderDTO> findByCustomerId(Long customerId){
//
//    }
//
//    public List<ServiceOrderDTO> findByServiceType(ServiceType type){
//
//    }

    private boolean checkIfIdExists(Long id){
        Optional<ServiceOrder> serviceOrderOptional = serviceOrderRepository.findById(id);
        return serviceOrderOptional.isPresent();
    }

}
