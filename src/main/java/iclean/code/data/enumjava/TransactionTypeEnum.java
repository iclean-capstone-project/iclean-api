package iclean.code.data.enumjava;

public enum TransactionTypeEnum {
    DEPOSIT ("Nạp tiền thành công"),
    WITHDRAW ("Rút tiền thành công"),
    TRANSFER ("Thanh toán thành công");

    private final String value;

    private TransactionTypeEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
