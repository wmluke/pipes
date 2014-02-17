package app.api;

public class ApiError {

    private final ApiErrorCode code;
    private final String message;

    public ApiError(ApiErrorCode statusCode, String message) {
        this.code = statusCode;
        this.message = message;
    }

    public ApiErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
