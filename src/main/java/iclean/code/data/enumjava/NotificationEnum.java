package iclean.code.data.enumjava;

public enum NotificationEnum {
    IS_EMPLOYEE(true),
    NOT_EMPLOYEE(false);

    private boolean value;

    NotificationEnum(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
