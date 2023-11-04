package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum BookingStatusEnum {
    REJECTED(0),///
    NOT_YET(1),
    APPROVED (2),//
    EMPLOYEE_ACCEPTED(3),//
    RENTER_ASSIGNED(4),//
    RENTER_CANCELED(5),//
    EMPLOYEE_CANCELED(6),//
    WAITING(7),
    IN_PROCESSING(8),//
    FINISHED(9),
    ON_CART(10);


    private final int value;

    private BookingStatusEnum(int value) {
        this.value = value;
    }
}
