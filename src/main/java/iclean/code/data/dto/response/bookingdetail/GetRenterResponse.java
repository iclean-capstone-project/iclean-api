package iclean.code.data.dto.response.bookingdetail;

import lombok.Data;

@Data
public class GetRenterResponse {
    private Integer renterId;
    private String renterName;
    private String renterAvatar;
    private String phoneNumber;
}