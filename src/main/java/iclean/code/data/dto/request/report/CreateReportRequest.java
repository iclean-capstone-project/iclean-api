package iclean.code.data.dto.request.report;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequest {
    private Integer bookingId;

    private Integer reportTypeId;

    private String detail;

    private List<MultipartFile> files;
}
