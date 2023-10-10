package iclean.code.data.enumjava;

public enum DeleteStatusEnum {
    ACTIVE(false),
    INACTIVE(true);

    private final Boolean value;

    DeleteStatusEnum(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}
