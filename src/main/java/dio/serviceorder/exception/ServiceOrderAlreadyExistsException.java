package dio.serviceorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ServiceOrderAlreadyExistsException extends Exception{

    public ServiceOrderAlreadyExistsException(Long id) {
        super(String.format("Service order with id %d is already created", id));
    }

}
