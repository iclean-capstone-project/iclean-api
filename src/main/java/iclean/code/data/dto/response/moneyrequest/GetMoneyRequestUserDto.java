package iclean.code.data.dto.response.moneyrequest;

import iclean.code.data.dto.response.PageResponseObject;
import lombok.Data;

@Data
public class GetMoneyRequestUserDto {
    private Integer userId;
    private String phoneNumber;
    private String fullName;
    private String roleName;
    private String dateOfBirth;
    private String email;
    private String facebookUid;
    private PageResponseObject data;
}
