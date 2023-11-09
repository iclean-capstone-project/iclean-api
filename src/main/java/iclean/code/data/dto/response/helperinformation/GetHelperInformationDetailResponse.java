package iclean.code.data.dto.response.helperinformation;

import iclean.code.data.dto.response.serviceregistration.GetServiceOfHelperResponse;
import lombok.Data;

import java.util.List;

@Data
public class GetHelperInformationDetailResponse {
    private Integer helperInformationId;
    private String personalAvatar;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String placeOfResidence;
    private String homeTown;
    private String fullName;
    private String status;
    private List<String> attachments;
    private List<GetServiceOfHelperResponse> services;
}
