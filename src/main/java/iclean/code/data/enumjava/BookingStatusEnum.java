package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum BookingStatusEnum {
    DONE(1),
    WAITING(2),
    IN_PROCESS (3),
    CANCEL(4);

    private int value;

    private BookingStatusEnum(int value) {
        this.value = value;
    }
}
