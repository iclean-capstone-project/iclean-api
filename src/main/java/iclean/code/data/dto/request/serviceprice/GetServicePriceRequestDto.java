package iclean.code.data.dto.request.serviceprice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetServicePriceRequestDto {
    private Integer serviceUnitId;
    private LocalTime startTime;
}
