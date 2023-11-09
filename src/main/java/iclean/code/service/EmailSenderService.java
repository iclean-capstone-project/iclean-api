package iclean.code.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.others.SendMailRequest;
import org.springframework.http.ResponseEntity;

public interface EmailSenderService {
    ResponseEntity<ResponseObject> sendEmail(SendMailRequest mail);

    void sendEmailWithHtmlTemplate(SendMailRequest mail);
}
