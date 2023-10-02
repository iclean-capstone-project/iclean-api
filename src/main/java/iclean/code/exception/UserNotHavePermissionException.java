package iclean.code.exception;

public class UserNotHavePermissionException extends Exception {
    public UserNotHavePermissionException(String errorMessage) {
        super(errorMessage);
    }
    public UserNotHavePermissionException() {
        super("User not have permission to do this action");
    }
    public UserNotHavePermissionException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
