package iclean.code.service.impl;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.others.SendMailRequest;
import iclean.code.data.enumjava.EmailEnum;
import iclean.code.service.EmailSenderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
@Log4j2
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("classpath:template-inprocess")
    private Resource resourceInProcess;

    @Value("classpath:template-report-result")
    private Resource resourceReportResult;

    @Value("classpath:template-accept-helper")
    private Resource resourceAcceptHelper;

    @Value("classpath:template-reject-helper")
    private Resource resourceRejectHelper;


    public ResponseEntity<ResponseObject> sendEmailTemplate( String options, SendMailRequest mail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            ClassPathResource logo = new ClassPathResource("static/img/iClean_logo.png");

            helper.setTo(mail.getTo());
            helper.setFrom("iclean.service2001@gmail.com", "IcleanService");

            String htmlContent = templateEngine.process(Objects.requireNonNull(resource.getFilename()), context);
            helper.setText(htmlContent, true);


            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendEmailAcceptReport(SendMailRequest mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            Context context = new Context();
            context.setVariable("company_name", EmailEnum.COMPANY_NAME.getValue());
            String htmlContent = "";
            switch (options) {
                case "ACCEPT_HELPER":
                    context.setVariable("name", mail.getHelperFullName());
                    context.setVariable("serviceName", mail.getServiceName());
                    helper.setSubject(EmailEnum.ACCEPT_HELPER_TITLE.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceAcceptHelper.getFilename()), context);
                    break;
                case "REJECT_HELPER" :
                    context.setVariable("name", mail.getHelperFullName());
                    helper.setSubject(EmailEnum.ACCEPT_HELPER_TITLE.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceRejectHelper.getFilename()), context);
                    break;
                case "REPORT_RESULT":
                    context.setVariable("name", mail.getRenterFullName());
                    context.setVariable("bookingId", mail.getBookingId());
                    context.setVariable("status", mail.getStatus());
                    helper.setSubject(EmailEnum.REPORT_RESULT_TITLE.getValue() + mail.getBookingId());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceReportResult.getFilename()), context);
                    break;
                default:
                    context.setVariable("name", mail.getTo());
                    context.setVariable("body", EmailEnum.IN_PROCESS_BODY.getValue());
                    helper.setSubject(EmailEnum.IN_PROCESS_TITLE.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceInProcess.getFilename()), context);
                    break;
            }

//            context.setVariable("name", mail.getHelperFullName());
//            helper.setSubject(EmailEnum.ACCEPT_HELPER_TITLE.getValue());
//            String htmlContent = templateEngine.process(Objects.requireNonNull(resourceRejectHelper.getFilename()), context);
            helper.setText(htmlContent, true);
            helper.addInline("logo", logo);

            mailSender.send(mimeMessage);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), "Send Email Success!", new MimeMessage(mimeMessage)));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}