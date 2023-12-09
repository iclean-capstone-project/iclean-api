package iclean.code.data.dto.request.helperinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmHelperRequestSendMail {
    private String to;
    private List<String> listOfJobs;
    private String helperName;
    private String managerName;
}
