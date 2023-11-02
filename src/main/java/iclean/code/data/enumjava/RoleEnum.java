package iclean.code.data.enumjava;

public enum RoleEnum {
    RENTER(1),
    EMPLOYEE(2),
    MANAGER(3),
    ADMIN(4);

    private int value;

    private RoleEnum(int value) {
        this.value = value;
    }
    public Integer getValue() {
        return value;
    }
}
