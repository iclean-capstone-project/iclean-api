package iclean.code.data.enumjava;

public enum EmailEnum {
    COMPANY_NAME("Helping Hand Hub"),

    IN_PROCESS_TITLE ("Đang phê duyệt"),

    IN_PROCESS_BODY ("Yêu cầu của bạn đang được phê duyệt, vui lòng đợi cho đến khi có thông báo chính thức. "),

    REPORT_RESULT_TITLE ("Kết quả báo cáo dịch vụ #"),

    ACCEPT_HELPER_TITLE("Kết quả đăng kí");


    private final String value;

    private EmailEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
