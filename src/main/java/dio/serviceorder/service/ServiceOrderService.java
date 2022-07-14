package dio.serviceorder.service;

import dio.serviceorder.dto.ServiceOrderDTO;
import dio.serviceorder.enums.ServiceType;
import dio.serviceorder.exception.ServiceOrderAlreadyExistsException;
import dio.serviceorder.exception.ServiceOrderNotFoundException;
import dio.serviceorder.mapper.ServiceOrderMapper;
import dio.serviceorder.model.Customer;
import dio.serviceorder.model.ServiceOrder;
import dio.serviceorder.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceOrderService {
    @Autowired
    ServiceOrderRepository serviceOrderRepository;

    ServiceOrderMapper serviceOrderMapper = ServiceOrderMapper.INSTANCE;

    public ServiceOrderDTO create(ServiceOrderDTO serviceOrderDTO) throws ServiceOrderAlreadyExistsException {
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);
        if (serviceOrderDTO.getId() != null && checkIfServiceOrderExists(serviceOrderDTO.getId()))
            throw new ServiceOrderAlreadyExistsException(serviceOrderDTO.getId());
        ServiceOrder savedServiceOrder = serviceOrderRepository.save(serviceOrder);
        return serviceOrderMapper.toDTO(savedServiceOrder);
    }

    public void deleteById(Long id) throws ServiceOrderNotFoundException {
        if (!checkIfServiceOrderExists(id))
            throw new ServiceOrderNotFoundException(id);
        serviceOrderRepository.deleteById(id);
    }

    public ServiceOrderDTO update(ServiceOrderDTO serviceOrderDTO) throws ServiceOrderNotFoundException {
        if (!checkIfServiceOrderExists(serviceOrderDTO.getId()))
            throw new ServiceOrderNotFoundException(serviceOrderDTO.getId());
        ServiceOrder serviceOrder = serviceOrderMapper.toModel(serviceOrderDTO);
        ServiceOrder savedServiceOrder = serviceOrderRepository.save(serviceOrder);
        return serviceOrderMapper.toDTO(savedServiceOrder);
    }

    public ServiceOrderDTO findById(Long id) throws ServiceOrderNotFoundException {
        ServiceOrder foundServiceOrder = serviceOrderRepository.findById(id)
                .orElseThrow(()-> new ServiceOrderNotFoundException(id));
        return serviceOrderMapper.toDTO(foundServiceOrder);
    }

    public List<ServiceOrderDTO> listAll(){
        return serviceOrderRepository.findAll()
                .stream()
                .map(serviceOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceOrderDTO> listAllWithClosedStatus(Boolean closedStatus){
        return serviceOrderRepository.findByClosed(closedStatus)
                .stream()
                .map(serviceOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceOrderDTO> listAllOfCustomer(Customer customer){
        return serviceOrderRepository.findByCustomer(customer)
                .stream()
                .map(serviceOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceOrderDTO> listAllWithServiceType(ServiceType type){
        return serviceOrderRepository.findByServiceType(type)
                .stream()
                .map(serviceOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    private boolean checkIfServiceOrderExists(Long id){
        Optional<ServiceOrder> serviceOrderOptional = serviceOrderRepository.findById(id);
        return serviceOrderOptional.isPresent();
    }

}
