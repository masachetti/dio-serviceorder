package dio.serviceorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceOrderNotFoundException extends Exception{
    public ServiceOrderNotFoundException(Long id) {
        super(String.format("Service order with id %s not found.", id));
    }
}
