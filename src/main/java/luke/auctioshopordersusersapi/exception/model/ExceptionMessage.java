package luke.auctioshopordersusersapi.exception.model;

import java.sql.Timestamp;

public class ExceptionMessage {
    private Timestamp timestamp;
    private String message;
    private int status;

    public ExceptionMessage() {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
