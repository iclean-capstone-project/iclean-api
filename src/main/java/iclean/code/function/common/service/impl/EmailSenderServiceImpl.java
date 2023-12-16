package iclean.code.function.common.service.impl;

import iclean.code.data.dto.request.helperinformation.AcceptHelperRequest;
import iclean.code.data.dto.request.helperinformation.CancelHelperRequestSendMail;
import iclean.code.data.dto.request.helperinformation.ConfirmHelperRequestSendMail;
import iclean.code.data.dto.request.others.SendMailRequest;
import iclean.code.data.dto.response.report.ReportHelperResultResponse;
import iclean.code.data.dto.response.report.ReportResultResponse;
import iclean.code.data.enumjava.EmailEnum;
import iclean.code.data.enumjava.SendMailOptionEnum;
import iclean.code.function.common.service.EmailSenderService;
import iclean.code.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("classpath:template-in-process")
    private Resource resourceInProcess;

    @Value("classpath:template-report-result")
    private Resource resourceReportResult;

    @Value("classpath:template-report-result-helper")
    private Resource resourceReportResultHelper;

    @Value("classpath:template-accept-helper")
    private Resource resourceAcceptHelper;

    @Value("classpath:template-reject-helper")
    private Resource resourceRejectHelper;

    @Value("classpath:template-confirm-helper")
    private Resource resourceConfirmHelper;

    @Override
    public void sendEmailTemplate(SendMailOptionEnum option, Object mailRequest) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom("iclean.service2001@gmail.com", "IcleanService");

            Context context = new Context();
            context.setVariable("company_name", EmailEnum.COMPANY_NAME.getValue());
            String htmlContent = "";
            switch (option) {
                case CONFIRM_HELPER:
                    ConfirmHelperRequestSendMail confirmHelperRequestSendMail = (ConfirmHelperRequestSendMail) mailRequest;
                    helper.setTo(confirmHelperRequestSendMail.getTo());
                    context.setVariable("name", confirmHelperRequestSendMail.getHelperName());
                    context.setVariable("manager_name", confirmHelperRequestSendMail.getManagerName());
                    context.setVariable("company_name", EmailEnum.COMPANY_NAME.getValue());
                    context.setVariable("list_of_jobs", confirmHelperRequestSendMail.getListOfJobs());
                    helper.setSubject(EmailEnum.CONFIRM_HELPER_TITLE.getValue() + EmailEnum.COMPANY_NAME.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceConfirmHelper.getFilename()), context);
                    break;
                case ACCEPT_HELPER:
                    AcceptHelperRequest request = (AcceptHelperRequest) mailRequest;
                    helper.setTo(request.getTo());
                    context.setVariable("name", request.getHelperName());
                    context.setVariable("location", EmailEnum.LOCATION.getValue());
                    context.setVariable("manager_name", request.getManagerName());
                    context.setVariable("company_name", EmailEnum.COMPANY_NAME.getValue());
                    context.setVariable("date_meeting", Utils.getLocalDateAsString(request.getDateMeeting().toLocalDate()) + " vào lúc: " +
                            Utils.getLocalTimeAsString(request.getDateMeeting().toLocalTime()));
                    helper.setSubject(EmailEnum.ACCEPT_HELPER_TITLE.getValue() + EmailEnum.COMPANY_NAME.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceAcceptHelper.getFilename()), context);
                    break;
                case REJECT_HELPER :
                    CancelHelperRequestSendMail cancelHelperRequestSendMail = (CancelHelperRequestSendMail) mailRequest;
                    helper.setTo(cancelHelperRequestSendMail.getTo());
                    context.setVariable("helper_name", cancelHelperRequestSendMail.getHelperName());
                    context.setVariable("manager_name", cancelHelperRequestSendMail.getManagerName());
                    context.setVariable("reject_reason", cancelHelperRequestSendMail.getRejectReason());
                    context.setVariable("company_name", EmailEnum.COMPANY_NAME.getValue());
                    helper.setSubject(EmailEnum.REJECT_HELPER_TITLE.getValue() + EmailEnum.COMPANY_NAME.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceRejectHelper.getFilename()), context);
                    break;
                case REPORT_RESULT:
                    ReportResultResponse mail = (ReportResultResponse) mailRequest;
                    helper.setTo(mail.getTo());
                    context.setVariable("renter_name", mail.getRenterName());
                    context.setVariable("booking_code", mail.getBookingCode());
                    context.setVariable("content_report", mail.getContentReport());
                    context.setVariable("solution", mail.getSolution());
                    context.setVariable("status", mail.getStatus());
                    context.setVariable("money_refund", mail.getMoneyRefund());
                    context.setVariable("point_refund", mail.getPointRefund());
                    context.setVariable("manager_name", mail.getManagerName());
                    helper.setSubject(EmailEnum.REPORT_RESULT_TITLE.getValue() + mail.getBookingCode());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceReportResult.getFilename()), context);
                    break;
                case REPORT_RESULT_HELPER:
                    ReportHelperResultResponse mailResultHelper = (ReportHelperResultResponse) mailRequest;
                    helper.setTo(mailResultHelper.getTo());
                    context.setVariable("helper_name", mailResultHelper.getHelperName());
                    context.setVariable("bookingCode", mailResultHelper.getBookingCode());
                    context.setVariable("createAt",Utils.getLocalDateAsString(mailResultHelper.getCreateAt().toLocalDate()) + " vào lúc: " +
                            Utils.getLocalTimeAsString(mailResultHelper.getCreateAt().toLocalTime()));
                    context.setVariable("content_report", mailResultHelper.getContentReport());
                    context.setVariable("solution", mailResultHelper.getSolution());
                    context.setVariable("money_pen", mailResultHelper.getMoneyPen());
                    context.setVariable("manager_name", mailResultHelper.getManagerName());
                    helper.setSubject(EmailEnum.REPORT_RESULT_TITLE.getValue() + mailResultHelper.getBookingCode());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceReportResultHelper.getFilename()), context);
                    break;
                default:
                    SendMailRequest mailRe = (SendMailRequest) mailRequest;
                    context.setVariable("name", mailRe.getTo());
                    context.setVariable("body", EmailEnum.IN_PROCESS_BODY.getValue());
                    helper.setSubject(EmailEnum.IN_PROCESS_TITLE.getValue());
                    htmlContent = templateEngine.process(Objects.requireNonNull(resourceInProcess.getFilename()), context);
                    break;
            }

//            context.setVariable("name", mail.getHelperFullName());
//            helper.setSubject(EmailEnum.ACCEPT_HELPER_TITLE.getValue());
//            String htmlContent = templateEngine.process(Objects.requireNonNull(resourceRejectHelper.getFilename()), context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
        }
    }

}