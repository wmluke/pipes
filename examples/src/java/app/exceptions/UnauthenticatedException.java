package app.exceptions;

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException() {
    }

    public UnauthenticatedException(String message) {
        super(message);
    }
}
