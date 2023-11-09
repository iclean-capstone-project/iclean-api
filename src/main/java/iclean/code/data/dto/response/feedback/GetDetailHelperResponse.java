package iclean.code.data.dto.response.feedback;

import lombok.Data;

@Data
public class GetDetailHelperResponse {
    private Integer serviceId;
    private Integer helperId;
    private String helperName;
    private String helperAvatar;
    private String serviceName;
    private String phoneNumber;
    private String serviceIcon;
    private Double avgRate;
    private Long numberOfFeedback;
}