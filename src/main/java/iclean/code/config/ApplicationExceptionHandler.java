package iclean.code.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.exception.InvalidJsonFormatException;
import iclean.code.exception.UserNotFoundException;
import iclean.code.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        "Some fields are invalid", errorMap));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ResponseObject> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getConstraintViolations().forEach(error -> {
            errorMap.put(Utils.subStringLastIndex(error.getPropertyPath().toString(), "."), error.getMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        "Some fields are invalid", errorMap));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseObject> handleBusinessException(UserNotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                        "Not found", errorMap));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseObject> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errorMap = new HashMap<>();
        String fieldName = ex.getName();
        Class<?> requiredType = ex.getRequiredType();
        String errorMessage = "Field '" + fieldName + "' requires type '" + Utils.subStringLastIndex(requiredType.getName(), ".") + "'.";
        errorMap.put(fieldName, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        "Some fields are invalid", errorMap));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseObject> handleJsonParseException(HttpMessageNotReadableException ex) {
        InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
        String message = "Invalid JSON format: " + invalidFormatException.getMessage();

        Map<String, String> errorMap = new HashMap<>();
        // Extract the field name from the path
        String fieldName = invalidFormatException.getPath().get(0).getFieldName();
        errorMap.put(fieldName, message);

        // Map the field name to the list of errors
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        "Invalid JSON format", errorMap));
    }

    @ExceptionHandler(InvalidJsonFormatException.class)
    public ResponseEntity<ResponseObject> handleInvalidJsonFormatException(InvalidJsonFormatException ex) {
        String message = "Invalid JSON format: " + ex.getMessage();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        "Invalid JSON format", errorMap));
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ResponseObject> handleJsonParseException(JsonParseException ex) {
        String message = "Invalid JSON format: " + ex.getMessage();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        "Invalid JSON format", errorMap));
    }
}
