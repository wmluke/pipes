package app.api;

import app.exceptions.ApiErrorException;

public class ApiResponse<M> {

    private final M body;
    private final ApiError error;

    private ApiResponse(M body, ApiError error) {
        this.body = body;
        this.error = error;
    }

    public M getBody() {
        return body;
    }

    public ApiError getError() {
        return error;
    }

    public static <M> ApiResponse<M> body(M model) {
        return new ApiResponse<>(model, null);
    }

    public static <M> ApiResponse<M> error(ApiErrorException e) {
        return new ApiResponse<>(null, new ApiError(e.getStatusCode(), e.getMessage()));
    }

    public static <M> ApiResponse<M> error(int statusCode, String message) {
        return new ApiResponse<>(null, new ApiError(statusCode, message));
    }

}
