package app.api;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;

public class ApiConstraintError extends ApiError {
    private List<InputViolation> inputViolations = new ArrayList<>();

    public ApiConstraintError() {
        super(ApiErrorCode.INVALID_FORMAT, "Invalid format");
    }

    public ApiConstraintError(String message) {
        super(ApiErrorCode.INVALID_FORMAT, message);
    }

    public List<InputViolation> getInputViolations() {
        return inputViolations;
    }

    public void addConstraintViolation(ConstraintViolation constraintViolation) {
        InputViolation inputViolation = new InputViolation();
        inputViolation.message = constraintViolation.getMessage();
        inputViolation.property = Joiner.on(".").join(constraintViolation.getPropertyPath());
        inputViolations.add(inputViolation);
    }

    @Override
    public String getMessage() {
        if (inputViolations.isEmpty()) {
            return super.getMessage();
        }
        List<String> errorMessages = Lists.transform(new ArrayList<>(inputViolations), new Function<InputViolation, String>() {
            @Override
            public String apply(InputViolation input) {
                return input.message;
            }
        });
        return Joiner.on(", ").skipNulls().join(errorMessages);
    }

    public static class InputViolation {
        public String message;
        public String property;
    }
}
