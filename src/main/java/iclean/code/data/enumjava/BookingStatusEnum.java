package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum BookingStatusEnum {
    REJECT(0),///
    NOT_YET(1),
    APPROVED (2),//
    EMPLOYEE_ACCEPT(3),//
    RENTER_ASSIGN(4),//
    RENTER_CANCEL(5),//
    EMPLOYEE_CANCEL(6),//
    WAITING(7),
    IN_PROCESS(8),//
    FINISH(9);


    private int value;

    private BookingStatusEnum(int value) {
        this.value = value;
    }
}
