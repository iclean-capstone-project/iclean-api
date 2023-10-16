package iclean.code.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExternalApiService {
    public String scanNationId(MultipartFile file);
}
