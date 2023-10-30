package iclean.code.data.enumjava;

import lombok.Getter;

@Getter
public enum NotificationStatusEnum {
    READ(true),
    NOT_READ(false);

    private final boolean value;

    private NotificationStatusEnum(boolean value) {
        this.value = value;
    }
}
