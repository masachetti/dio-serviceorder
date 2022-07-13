package dio.serviceorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomerAlreadyExistsException extends Exception{
    public CustomerAlreadyExistsException(Long id) {
        super(String.format("Customer with id %d is already created", id));
    }
}
