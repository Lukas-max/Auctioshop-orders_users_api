package luke.auctioshopordersusersapi.exception.handler;

import luke.auctioshopordersusersapi.exception.OrderNotFoundException;
import luke.auctioshopordersusersapi.exception.model.ErrorValidationResponse;
import luke.auctioshopordersusersapi.exception.model.ExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.add(fieldError.getDefaultMessage());
        });

        ErrorValidationResponse response = new ErrorValidationResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setTimestamp(new Timestamp(System.currentTimeMillis()));
        response.setValidationErrors(errors);

        log.warn("Handled MethodArgumentNotValidException - invalid data, sending exception message.");
        return handleExceptionInternal(ex, response, headers, response.getStatus(), request);
    }

    /**
     *
     * Handles OrderNotFoundException from OrderController.getOrderById(Long id);
     * Now, a user wont see a full stack trace when typing wrong order id on /api/order/{id} ,
     * but an info he will clearly understand.
     */
    @ExceptionHandler(value = OrderNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleOrderNotFoundException(OrderNotFoundException ex){
        ExceptionMessage message = new ExceptionMessage();
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        message.setStatus(HttpStatus.NOT_FOUND.value());
        message.setMessage(ex.getMessage());

        log.warn("Handled OrderNotFoundException - sending exception message.");
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles SpelEvaluationException. In our cause from OrderController.getOrderByOrderId(Long id).
     * When authenticated user tries to call other non registered user order we catch the exception being thrown
     * due to null fields on returned object.
     * In other case we log the error message.
     */
    @ExceptionHandler(value = SpelEvaluationException.class)
    public ResponseEntity<?> handleSpelEvaluationException(SpelEvaluationException ex){
        if (ex.getMessageCode() == SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL){
            log.warn("Handled SpelEvaluationException - sending exception message.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
