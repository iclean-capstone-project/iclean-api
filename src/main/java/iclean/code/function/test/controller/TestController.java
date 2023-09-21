package iclean.code.function.test.controller;

import iclean.code.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private StorageService storageService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) {
        return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/file")
    public ResponseEntity<Boolean> delete(@RequestParam String url) {
        return new ResponseEntity<>(storageService.deleteFile(url), HttpStatus.CREATED);
    }
}
