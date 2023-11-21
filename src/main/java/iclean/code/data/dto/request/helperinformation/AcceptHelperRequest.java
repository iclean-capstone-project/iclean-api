package iclean.code.data.dto.request.helperinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptHelperRequest {
    private String to;
    private String helperName;
    private String managerName;
    private LocalDateTime dateMeeting;
}
