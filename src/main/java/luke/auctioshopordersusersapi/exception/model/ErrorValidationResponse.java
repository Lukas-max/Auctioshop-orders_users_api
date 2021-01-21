package luke.auctioshopordersusersapi.exception.model;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ErrorValidationResponse {
    private HttpStatus status;
    private Timestamp timestamp;
    private String message;
    private List<String> validationErrors;

    public ErrorValidationResponse() {
    }

    public ErrorValidationResponse(HttpStatus status, Timestamp timestamp, String message, List<String> errors) {
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
        this.validationErrors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = new ArrayList<>(validationErrors);
    }
}
