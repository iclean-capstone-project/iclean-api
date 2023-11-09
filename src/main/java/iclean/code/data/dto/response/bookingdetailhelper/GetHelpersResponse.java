package iclean.code.data.dto.response.bookingdetailhelper;

import lombok.Data;

@Data
public class GetHelpersResponse {
    private Integer serviceId;
    private Integer helperId;
    private String helperName;
    private String helperAvatar;
    private Double rate;
    private String phoneNumber;
    private Long numberOfFeedback;
}