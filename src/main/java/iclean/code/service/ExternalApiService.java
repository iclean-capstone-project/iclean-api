package iclean.code.service;

import iclean.code.data.dto.response.others.CMTApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ExternalApiService {
    public ResponseEntity<String> scanNationId(MultipartFile file);
}
