package iclean.code.data.enumjava;

import iclean.code.exception.NotFoundException;

public enum ServicePriceEnum {
    FIRST(1, "00:00:00", "06:00:00"),
    SECOND(2, "06:00:00", "10:00:00"),
    THIRD(3, "10:00:00", "14:00:00"),
    FOURTH(4, "14:00:00", "18:00:00"),
    FIFTH(5, "18:00:00", "22:00:00"),
    SIXTH(6, "22:00:00", "23:59:59");
    private final Integer id;
    private final String startDate;
    private final String endDate;
    private ServicePriceEnum(Integer id, String startDate, String endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Integer getId() {
        return id;
    }

    public static ServicePriceEnum getById(Integer id) {
        for (ServicePriceEnum serviceEnum : ServicePriceEnum.values()) {
            if (serviceEnum.getId().equals(id)) {
                return serviceEnum;
            }
        }
        throw new NotFoundException("No enum constant with id: " + id);
    }
}
