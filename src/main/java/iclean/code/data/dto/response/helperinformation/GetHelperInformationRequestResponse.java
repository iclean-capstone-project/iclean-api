package iclean.code.data.dto.response.helperinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetHelperInformationRequestResponse {
    private Integer helperInformationId;
    private String personalAvatar;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String placeOfResidence;
    private String homeTown;
    private String fullName;
    private String status;
}
