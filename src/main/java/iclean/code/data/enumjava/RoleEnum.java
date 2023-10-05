package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum RoleEnum {
    RENTER("renter"),
    EMPLOYEE("employee"),
    MANAGER("manager"),
    ADMIN("admin");

    private final String value;

    private RoleEnum(String value) {
        this.value = value;
    }
}
