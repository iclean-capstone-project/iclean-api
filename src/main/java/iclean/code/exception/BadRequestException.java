package iclean.code.exception;

public class BadRequestException extends Exception {
    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }
    public BadRequestException() {
        super("Bad Request Exception: Some error of the request");
    }
    public BadRequestException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
