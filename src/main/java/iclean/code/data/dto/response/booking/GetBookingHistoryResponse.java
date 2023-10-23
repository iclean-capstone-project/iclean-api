package iclean.code.data.dto.response.booking;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GetBookingHistoryResponse {

    private String employeeFullName;

    private String renterFullName;

    private String jobName;

    private LocalDateTime workDate;

    private String locationDescription;

}
