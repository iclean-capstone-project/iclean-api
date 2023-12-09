package iclean.code.data.enumjava;

public enum ServiceHelperStatusEnum {
    ACTIVE,
    //helper have permission to do this service but they turn off the service
    INACTIVE,
    WAITING_FOR_APPROVE,
    // helper do not have permission to do this service
    DISABLED,
    APPROVED_WITH_CONDITION
}
