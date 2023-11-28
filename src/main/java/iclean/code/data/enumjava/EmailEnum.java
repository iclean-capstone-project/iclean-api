package iclean.code.data.enumjava;

public enum EmailEnum {
    COMPANY_NAME("Helping Hands Hub Platform"),
    IN_PROCESS_TITLE ("Đang phê duyệt"),
    LOCATION("Tòa nhà 68 phòng 202, Khu công nghệ cao, D1, Thành phố Thủ Đức, Thành phố Hồ Chí Minh"),
    IN_PROCESS_BODY ("Yêu cầu của bạn đang được phê duyệt, vui lòng đợi cho đến khi có thông báo chính thức. "),
    REPORT_RESULT_TITLE ("Kết quả báo cáo dịch vụ #"),
    ACCEPT_HELPER_TITLE("Hẹn gặp trao đổi thông tin - "),
    CONFIRM_HELPER_TITLE("Kết quả đăng ký trở thành nhân viên - "),
    REJECT_HELPER_TITLE("Trạng thái đăng ký - ");


    private final String value;

    private EmailEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
