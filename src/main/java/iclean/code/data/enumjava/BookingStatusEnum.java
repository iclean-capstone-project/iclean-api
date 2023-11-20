package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum BookingStatusEnum {
    REJECTED(0, "Từ chối"),///
    NOT_YET(1, "Đang đợi"),
    APPROVED (2, "Đã được chấp nhận"),//
    EMPLOYEE_ACCEPTED(3, "Đã nhận"),//
    RENTER_ASSIGNED(4, "Đã chọn nhân viên"),//
    RENTER_CANCELED(5, "Cancel"),//
    EMPLOYEE_CANCELED(6, "Bị hủy bởi nhân viên"),//
    WAITING(7, "Đang đợi"),
    IN_PROCESSING(8, "Đang làm"),//
    FINISHED(9, "Đã hoàn thành"),
    ON_CART(10, "On cart"),
    REPORTED(11, "Bị báo cáo");

    private final int label;
    private final String value;

    private BookingStatusEnum(int label, String value) {
        this.label = label;
        this.value = value;
    }
}
