package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum StatusNotification {
    READ(true),
    NOT_READ(false);

    private final boolean value;

    private StatusNotification(boolean value) {
        this.value = value;
    }
}
