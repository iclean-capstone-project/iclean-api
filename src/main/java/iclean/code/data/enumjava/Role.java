package iclean.code.data.enumjava;

public enum Role {
    RENTER(1),
    EMPLOYEE(2),
    MANAGER(3),
    ADMIN(4);

    private int value;

    private Role(int value) {
        this.value = value;
    }
}
