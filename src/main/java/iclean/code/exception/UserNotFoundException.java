package iclean.code.exception;

public class UserNotFoundException extends Exception {
        public UserNotFoundException(String errorMessage) {
            super(errorMessage);
        }
        public UserNotFoundException() {
            super();
        }
    public UserNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
