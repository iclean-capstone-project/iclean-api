package iclean.code.data.dto.response.authen;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserInformationDto {

    private String fullName;

    private String phoneNumber;

    private String roleName;

    private String dateOfBirth;

    private String defaultAddress;

    private String avatar;

    private Boolean isNewUser = false;
}
