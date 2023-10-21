package iclean.code.data.dto.response.profile;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileUserDto {
    private String fullName;

    private String phoneNumber;

    private LocalDateTime dateOfBirth;

    private String defaultAddress;

    private String avatar;
}
