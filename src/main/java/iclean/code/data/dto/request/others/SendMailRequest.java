package iclean.code.data.dto.request.others;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SendMailRequest {

    private String to;

    private String subject;

    private String body;

}
