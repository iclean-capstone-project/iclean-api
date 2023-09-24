package iclean.code.data.enumjava;

public enum Role {
    RENTER(1),
    EMPLOYEE(2);

    private int value;

    private Role(int value) {
        this.value = value;
    }
}
