package iclean.code.data.dto.request.authen;

import lombok.Data;

import java.util.List;

@Data
public class NotificationRequestDto {
    private List<String> target;
    private String title;
    private String body;
}