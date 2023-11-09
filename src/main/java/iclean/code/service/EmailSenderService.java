package iclean.code.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.others.SendMailRequest;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;

public interface EmailSenderService {

    ResponseEntity<ResponseObject> sendEmailTemplate( String options, SendMailRequest mail);
}
