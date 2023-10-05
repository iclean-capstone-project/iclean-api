package iclean.code.service.impl;

import iclean.code.service.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    @Value("${external.fpt.api.url}")
    private String fptAi;

    @Value("${external.fpt.api.key}")
    private String apiKey;
    @Override
    public ResponseEntity<String> scanNationId(MultipartFile file) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("api_name", apiKey);

// Load the file into a Resource (e.g., FileSystemResource)
            Resource fileResource = new FileSystemResource((File) file); // Assuming 'file' is a java.io.File

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("image", fileResource); // Pass the fileResource here

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(fptAi, requestEntity, String.class);

            return restTemplate.postForEntity(fptAi, requestEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }
}
