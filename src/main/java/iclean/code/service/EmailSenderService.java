package iclean.code.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.helperinformation.CancelHelperRequestSendMail;
import iclean.code.data.dto.request.others.SendMailRequest;
import iclean.code.data.enumjava.SendMailOptionEnum;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;

public interface EmailSenderService {

    void sendEmailTemplate(SendMailOptionEnum option, Object mail);

}
