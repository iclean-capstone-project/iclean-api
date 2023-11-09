package iclean.code.function.test.controller;

import iclean.code.data.dto.request.others.SendMailRequest;
import iclean.code.service.EmailSenderService;
import iclean.code.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private StorageService storageService;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) {
        return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.CREATED);
    }

    @GetMapping
    public String wish() {
        return "new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.CREATED);";
    }

    @DeleteMapping(value = "/file")
    public ResponseEntity<Boolean> delete(@RequestParam String url) {
        return new ResponseEntity<>(storageService.deleteFile(url), HttpStatus.CREATED);
    }

    @PostMapping("/send-html-email")
    public String sendHtmlEmail(@RequestParam(defaultValue = "IN_PROCESS") @Nullable String option,
                                @RequestBody SendMailRequest emailRequest) {
        emailSenderService.sendEmailTemplate(option, emailRequest);
        return "HTML email sent successfully!";
    }
}
