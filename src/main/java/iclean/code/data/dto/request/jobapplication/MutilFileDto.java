package iclean.code.data.dto.request.jobapplication;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MutilFileDto {
    @NotNull(message = "employee Id là bắt buộc")
    MultipartFile frontIdCard;
    @NotNull(message = "employee Id là bắt buộc")
    MultipartFile backIdCard;
    @NotNull(message = "employee Id là bắt buộc")
    MultipartFile avatar;
    List<MultipartFile> others;
}
