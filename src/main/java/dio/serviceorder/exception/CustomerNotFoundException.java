package dio.serviceorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(Long id) {
        super(String.format("Cannot found customer with id %d ", id));
    }

    public CustomerNotFoundException(String name) {
        super(String.format("Cannot found customer with name %s ", name));
    }
}
