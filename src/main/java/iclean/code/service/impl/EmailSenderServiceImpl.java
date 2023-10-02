package iclean.code.service.impl;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.others.SendMailRequest;
import iclean.code.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("classpath:email-template.html")
    private Resource resource;

    @Override
    public ResponseEntity<ResponseObject> sendEmail(SendMailRequest mail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mail.getTo());
            message.setSubject(mail.getSubject());
            message.setText(mail.getBody());
            mailSender.send(message);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), "Send Email Success!", new SimpleMailMessage(message)));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Send Mail Failed!", exception));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> sendEmailWithHtmlTemplate(SendMailRequest mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());

            Context context = new Context();
            context.setVariable("name", mail.getTo());
            context.setVariable("body", mail.getBody());

            String htmlContent = templateEngine.process(Objects.requireNonNull(resource.getFilename()), context);
            helper.setText(htmlContent, true);


            mailSender.send(mimeMessage);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), "Send Email Success!", new MimeMessage(mimeMessage)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Send Mail Failed!", e));
        }
    }
}