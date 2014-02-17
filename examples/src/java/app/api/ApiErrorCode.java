package app.api;

public enum ApiErrorCode {
    INVALID_FORMAT(422),
    RECORD_EXISTS(400),
    BAD_REQUEST(400),
    ASSIGNMENT_EXISTS(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    SERVER_ERROR(500);

    private final int httpStatus;

    private ApiErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public static ApiErrorCode valueOfStatusCode(int statusCode) {
        for (ApiErrorCode apiErrorCode : ApiErrorCode.values()) {
            if (apiErrorCode.httpStatus() == statusCode) {
                return apiErrorCode;
            }
        }
        return null;
    }
}
