package app.api;

import app.exceptions.ApiErrorException;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

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
        return new ApiResponse<>(null, new ApiError(ApiErrorCode.valueOfStatusCode(e.getStatusCode()), e.getMessage()));
    }

    public static <M> ApiResponse<M> error(ApiError error) {
        return new ApiResponse<>(null, error);
    }

    public static <M> ApiResponse<M> error(int statusCode, String message) {
        return new ApiResponse<>(null, new ApiError(ApiErrorCode.valueOfStatusCode(statusCode), message));
    }


    public static Middleware.StandardMiddleware4<HttpRequest, HttpResponse> toJson(int status) {
        return (req, res, next) -> {
            res.toJson(status, ApiResponse.body(next.memo()));
        };
    }

}
