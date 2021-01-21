package luke.auctioshopordersusersapi.exception.handler;

import luke.auctioshopordersusersapi.exception.OrderNotFoundException;
import luke.auctioshopordersusersapi.exception.model.ErrorValidationResponse;
import luke.auctioshopordersusersapi.exception.model.ExceptionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     *
     * @return global exception response for wrong validation
     * Message is sent to null intentionally. The user gets the error from validationErrors
     * field.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()){
            errors.add(error.getDefaultMessage());
        }

        ErrorValidationResponse response = new ErrorValidationResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setTimestamp(new Timestamp(System.currentTimeMillis()));
        response.setValidationErrors(errors);

        return handleExceptionInternal(ex, response, headers, response.getStatus(), request);
    }

    /**
     *
     * Handles OrderNotFoundException from OrderController.getOrderById(Long id);
     * Now, a user wont see a full stack trace when typing wrong order id on /api/order/{id} ,
     * but an info he will clearly understand.
     */
    @ExceptionHandler
    public ResponseEntity<ExceptionMessage> handleOrderNotFoundException(OrderNotFoundException ex){
        ExceptionMessage message = new ExceptionMessage();
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        message.setStatus(404);
        message.setMessage(ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
