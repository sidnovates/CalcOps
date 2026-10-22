package CalcOps.exception;

import CalcOps.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmeticException(
            ArithmeticException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                "DIVISION_BY_ZERO",
                "Cannot divide by zero"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                "MISSING_PARAMETER",
                "Required parameter '" + ex.getParameterName()
                        + "' is missing"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(
            MethodArgumentTypeMismatchException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_PARAMETER",
                "Parameter '" + ex.getName()
                        + "' must contain a valid number"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}