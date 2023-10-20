package iclean.code.data.dto.request.authen;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateProfileDto {

    private String fullName;

    private String dateOfBirth;

    private MultipartFile fileImage;
}
