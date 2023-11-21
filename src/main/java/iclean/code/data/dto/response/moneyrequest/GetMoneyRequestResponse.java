package iclean.code.data.dto.response.moneyrequest;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetMoneyRequestResponse {

    private Integer requestId;

    private LocalDateTime requestDate;

    private Double balance;

    private String requestStatus;

    private LocalDateTime processDate;

    private String requestType;

}
