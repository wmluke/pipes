package app.exceptions;

public class RecordNotFoundException extends ApiErrorException {

    public RecordNotFoundException() {
        super(404, "Record not found");
    }

    public RecordNotFoundException(String message) {
        super(404, message);
    }

}
