package iclean.code.function.common.service.impl;

import iclean.code.function.common.service.ExternalApiService;
import iclean.code.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    @Value("${external.fpt.api.url}")
    private String fptAi;

    @Value("${external.fpt.api.key}")
    private String apiKey;
    @Override
    public String scanNationId(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("api_key", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fptAi);
        URI uri = builder.build().encode().toUri();

        File tempFile = null;
        try {
            String extension = "." + Utils.subStringLastIndex(".", file.getOriginalFilename());
            tempFile = File.createTempFile("temp", extension);
            file.transferTo(tempFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("image", new FileSystemResource(tempFile));
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        String document = null;
        try {
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
            document = responseEntity.getBody();
        } catch (Exception e) {
            e.getMessage();
        }

        return document;

    }
}
