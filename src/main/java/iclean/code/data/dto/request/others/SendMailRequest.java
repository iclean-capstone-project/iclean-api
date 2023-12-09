package iclean.code.data.dto.request.others;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Data
@Getter
@Setter
public class SendMailRequest {

    private String to;

    @Nullable
    private String renterFullName;

    @Nullable
    private String helperFullName;

    @Nullable
    private String bookingId;

    @Nullable
    private String status;

    @Nullable
    private List<String> serviceName;
}
