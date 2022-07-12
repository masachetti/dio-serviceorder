package dio.serviceorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomerAlreadyCreated extends Exception{
    public CustomerAlreadyCreated(Long id) {
        super(String.format("Customer with id %d is already created", id));
    }
}
