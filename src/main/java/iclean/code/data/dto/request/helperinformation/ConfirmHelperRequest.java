package iclean.code.data.dto.request.helperinformation;

import lombok.Data;

import java.util.List;

@Data
public class ConfirmHelperRequest {
    List<Integer> serviceRegistrationIds;
}
