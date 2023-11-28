package iclean.code.data.dto.response.profile;

import iclean.code.utils.anotation.SortValue;
import lombok.Data;

@Data
public class UserResponse {
    private Integer userId;

    private String fullName;

    private String phoneNumber;

    @SortValue(value = "role.title")
    private String roleName;

    private String dateOfBirth;

    @SortValue(value = "address.description")
    private String defaultAddress;

    private String avatar;

    private Boolean isLocked;

    private String email;
}
