package iclean.code.data.dto.response.authen;

import lombok.Data;

@Data
public class UserInformationDto {

    private String fullName;

    private String phoneNumber;

    private String roleName;

    private String dateOfBirth;

    private String gender;

    private String avatar;

    private Boolean isNewUser = false;
}
