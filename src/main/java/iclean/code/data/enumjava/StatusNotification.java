package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum StatusNotification {
    READ(1),
    NOT_READ(2);

    private final int value;

    private StatusNotification(int value) {
        this.value = value;
    }
}
