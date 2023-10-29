package iclean.code.data.dto.response.profile;

import lombok.Data;

@Data
public class ProfileUserDto {
    private String fullName;

    private String phoneNumber;

    private String dateOfBirth;

    private String defaultAddress;

    private String avatar;
}
