package iclean.code.data.dto.request.helperinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelHelperRequestSendMail {
    private String to;
    private String helperName;
    private String rejectReason;
    private String managerName;
}
