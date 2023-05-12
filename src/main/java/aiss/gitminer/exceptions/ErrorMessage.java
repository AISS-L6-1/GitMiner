package aiss.gitminer.exceptions;

import java.util.Date;

public class ErrorMessage {
    private Date timestamp;
    private int statusCode;
    private String message;
    private String description;

    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}