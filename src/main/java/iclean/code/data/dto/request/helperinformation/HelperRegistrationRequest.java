package iclean.code.data.dto.request.helperinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
public class HelperRegistrationRequest {
    private String email;
    private MultipartFile frontIdCard;
    private MultipartFile backIdCard;
    private MultipartFile avatar;
    private List<MultipartFile> others;
    private List<Integer> serviceIds;
}
