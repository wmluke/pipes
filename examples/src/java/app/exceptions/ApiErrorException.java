package app.exceptions;

public class ApiErrorException extends RuntimeException {

    private final int statusCode;

    public ApiErrorException(int statusCode) {
        this.statusCode = statusCode;
    }

    public ApiErrorException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
