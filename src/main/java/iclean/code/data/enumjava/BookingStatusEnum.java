package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum BookingStatusEnum {
    ON_CART,
    NOT_YET,
    REJECTED,
    APPROVED,
    FINISHED,
    NO_MONEY,
    CANCELED
}