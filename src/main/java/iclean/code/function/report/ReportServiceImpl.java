package iclean.code.function.report;

import iclean.code.config.MessageVariable;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.report.CreateReportRequest;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.data.dto.response.report.ReportResultResponse;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetTransactionBookingResponse;
import iclean.code.data.dto.response.bookingdetail.GetAddressResponseBooking;
import iclean.code.data.dto.response.bookingdetail.GetBookingDetailDetailResponse;
import iclean.code.data.dto.response.bookingdetailhelper.GetHelpersResponse;
import iclean.code.data.dto.response.feedback.GetFeedbackResponse;
import iclean.code.data.dto.response.feedback.PointFeedbackOfHelper;
import iclean.code.data.dto.response.report.GetReportResponseAsManager;
import iclean.code.data.dto.response.report.GetReportResponseDetail;
import iclean.code.data.dto.response.service.PriceService;
import iclean.code.data.enumjava.*;
import iclean.code.data.repository.*;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.common.service.EmailSenderService;
import iclean.code.function.common.service.FCMService;
import iclean.code.function.feedback.service.FeedbackService;
import iclean.code.function.report.service.ReportService;
import iclean.code.function.common.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    BookingDetailHelperRepository bookingDetailHelperRepository;

    @Autowired
    private ReportTypeRepository reportTypeRepository;

    @Autowired
    private ReportAttachmentRepository reportAttachmentRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getReports(Integer userId, String renterName, Boolean displayAll, Pageable pageable) {
        try {
            Page<Report> reports;
            if (!displayAll) {
                reports = reportRepository.findReportsAsManager(userId, Utils.removeAccentMarksForSearching(renterName), pageable);
            } else {
                reports = reportRepository.findAllReportByRenterName(Utils.removeAccentMarksForSearching(renterName), pageable);
            }
            List<GetReportResponseAsManager> data = reports
                    .stream()
                    .map(report -> modelMapper.map(report, GetReportResponseAsManager.class))
                    .collect(Collectors.toList());
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(reports, data);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Report ", pageResponseObject));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getReportById(Integer reportId) {
        try {
            Report report = findReport(reportId);
            GetReportResponseDetail response = modelMapper.map(report, GetReportResponseDetail.class);
            BookingDetail bookingDetail = report.getBookingDetail();
            GetBookingDetailDetailResponse detailResponse = modelMapper.map(bookingDetail, GetBookingDetailDetailResponse.class);
            if (bookingDetail.getBooking().getBookingCode() != null)
                detailResponse.setBookingCode(bookingDetail.getBooking().getBookingCode());
            if (bookingDetail.getBooking().getRejectionReason() != null && bookingDetail.getBooking().getRjReasonDescription() != null) {
                detailResponse.setRejectionReasonContent(bookingDetail.getBooking().getRejectionReason().getRejectionContent());
                detailResponse.setRejectionReasonContent(bookingDetail.getBooking().getRjReasonDescription());
            }
            detailResponse.setServiceId(bookingDetail.getServiceUnit().getService().getServiceId());
            detailResponse.setServiceUnitId(bookingDetail.getServiceUnit().getServiceUnitId());
            detailResponse.setServiceIcon(bookingDetail.getServiceUnit().getService().getServiceImage());
            detailResponse.setServiceName(bookingDetail.getServiceUnit().getService().getServiceName());
            detailResponse.setValue(bookingDetail.getServiceUnit().getUnit().getUnitDetail());
            detailResponse.setEquivalent(bookingDetail.getServiceUnit().getUnit().getUnitValue());
            detailResponse.setPrice(bookingDetail.getPriceDetail());
            detailResponse.setCurrentStatus(bookingDetail.getBookingDetailStatus().name());
            GetAddressResponseBooking addressResponseBooking = modelMapper.map(bookingDetail.getBooking(), GetAddressResponseBooking.class);
            GetHelpersResponse getHelpersResponse = null;
            BookingDetailHelper bookingDetailHelper = null;
            List<BookingDetailHelper> bookingDetailHelpers = bookingDetailHelperRepository.findByBookingDetailIdAndActive(bookingDetail.getBookingDetailId(), BookingDetailHelperStatusEnum.ACTIVE);
            if (!bookingDetailHelpers.isEmpty()) {
                bookingDetailHelper = bookingDetailHelpers.get(0);
            }
            if (bookingDetailHelper != null) {
                getHelpersResponse = new GetHelpersResponse();
                User helper = bookingDetailHelper.getServiceRegistration().getHelperInformation().getUser();
                PointFeedbackOfHelper pointFeedbackOfHelper = feedbackService
                        .getDetailOfHelperFunction(bookingDetailHelper.getServiceRegistration().getHelperInformation().getUser().getUserId(),
                                bookingDetail.getServiceUnit().getServiceUnitId());
                getHelpersResponse.setServiceId(bookingDetail.getServiceUnit().getService().getServiceId());
                getHelpersResponse.setHelperId(helper.getUserId());
                getHelpersResponse.setHelperName(helper.getFullName());
                getHelpersResponse.setHelperAvatar(helper.getAvatar());
                getHelpersResponse.setRate(pointFeedbackOfHelper.getRate());
                getHelpersResponse.setNumberOfFeedback(pointFeedbackOfHelper.getNumberOfFeedback());
                getHelpersResponse.setPhoneNumber(helper.getPhoneNumber());
            }

            GetTransactionBookingResponse transactionBookingResponse = new GetTransactionBookingResponse();
            Transaction transactionMoney = transactionRepository
                    .findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(bookingDetail.getBooking().getBookingId(),
                            WalletTypeEnum.MONEY, TransactionTypeEnum.WITHDRAW, bookingDetail.getBooking().getRenter().getUserId());
            List<PriceService> priceServices;
            if (transactionMoney != null) {
                priceServices = bookingDetail.getBooking().getBookingDetails()
                        .stream()
                        .map(element -> {
                            PriceService priceService = new PriceService();
                            priceService.setServiceName(element.getServiceUnit().getService().getServiceName());
                            priceService.setPrice(element.getPriceDetail());
                            return priceService;
                        })
                        .collect(Collectors.toList());
                transactionBookingResponse.setStatus(TransactionBookingStatusEnum.PAID.name());
                transactionBookingResponse.setTransactionCode(transactionMoney.getTransactionCode());
            } else {
                priceServices = bookingDetail.getBooking().getBookingDetails()
                        .stream()
                        .map(element -> {
                            PriceService priceService = new PriceService();
                            priceService.setServiceName(element.getServiceUnit().getService().getServiceName());
                            priceService.setPrice(element.getPriceDetail());
                            return priceService;
                        })
                        .collect(Collectors.toList());
                transactionBookingResponse.setStatus(TransactionBookingStatusEnum.UNPAID.name());
            }
            transactionBookingResponse.setTotalPrice(bookingDetail.getBooking().getTotalPrice());
            transactionBookingResponse.setTotalPriceActual(bookingDetail.getBooking().getTotalPriceActual());
            transactionBookingResponse.setDiscount(bookingDetail.getBooking().getTotalPrice() - bookingDetail.getBooking().getTotalPriceActual());
            transactionBookingResponse.setServicePrice(priceServices);
            GetFeedbackResponse feedback = null;
            if (bookingDetail.getFeedback() != null && !bookingDetail.getFeedback().isEmpty()) {
                feedback = modelMapper.map(bookingDetail, GetFeedbackResponse.class);
            }

            detailResponse.setAddress(addressResponseBooking);
            detailResponse.setHelper(getHelpersResponse);
            detailResponse.setTransaction(transactionBookingResponse);
            detailResponse.setFeedback(feedback);
            response.setBookingDetail(detailResponse);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Report type", response));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createReport(CreateReportRequest reportRequest, Integer renterId) {
        try {
            if (reportRepository.findReportByBookingDetailBookingDetailId(reportRequest.getBookingDetailId()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                MessageVariable.ALREADY_REPORTED, null));
            }
            BookingDetail bookingDetail = findBookingDetail(reportRequest.getBookingDetailId());
            if(!BookingDetailStatusEnum.FINISHED.equals(bookingDetail.getBookingDetailStatus())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                "Cannot report booking has not finished yet", null));
            }
            bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.REPORTED);
            if (!Objects.equals(bookingDetail.getBooking().getRenter().getUserId(), renterId))
                throw new UserNotHavePermissionException("User cannot do this action");
            Report report = mappingReportForCreate(reportRequest);
            reportRepository.save(report);

            List<String> images = new ArrayList<>(Collections.emptyList());
            if (Objects.nonNull(reportRequest.getFiles())) {
                for (MultipartFile file :
                        reportRequest.getFiles()) {
                    images.add(storageService.uploadFile(file));
                }
            }
            List<ReportAttachment> reportAttachments = new ArrayList<>();
            for (String imageLink :
                    images) {
                ReportAttachment reportAttachment = new ReportAttachment();
                reportAttachment.setReportAttachmentLink(imageLink);
                reportAttachment.setReport(report);
                reportAttachments.add(reportAttachment);
            }
            reportAttachmentRepository.saveAll(reportAttachments);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Report Successfully!", null));

        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User is not found!"));
    }

    @Override
    public ResponseEntity<ResponseObject> updateReport(int reportId, UpdateReportRequest reportRequest, Integer managerId) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            User manager = findById(managerId);
            ReportResultResponse reportResultResponse = new ReportResultResponse();
            Report report = findReport(reportId);
            BookingDetail bookingDetail = report.getBookingDetail();
            bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.FINISHED);
            List<BookingDetailHelper> bookingDetailHelpers = bookingDetailHelperRepository.findByBookingDetailIdAndActive(report.getBookingDetail().getBookingDetailId(),
                    BookingDetailHelperStatusEnum.ACTIVE);
            HelperInformation helperInformation = null;
            if (!bookingDetailHelpers.isEmpty()) {
                BookingDetailHelper bookingDetailHelper = bookingDetailHelpers.get(0);
                helperInformation = bookingDetailHelper.getServiceRegistration().getHelperInformation();
            }

            User renter = report.getBookingDetail().getBooking().getRenter();
            String solution = "";
            reportResultResponse.setContentReport(report.getDetail());
            reportResultResponse.setBookingCode(report.getBookingDetail().getBooking().getBookingCode());
            if (reportRequest.getRefundPercent() == 0) {
                if (reportRequest.getReason() == null || reportRequest.getReason().isEmpty()) {
                    throw new BadRequestException("Need add reason to report");
                }
                solution = String.format(MessageVariable.REJECT_REPORT,
                        reportRequest.getReason());
                reportResultResponse.setStatus(ReportStatusEnum.REJECTED.name());
                reportResultResponse.setSolution(solution);
                report.setReportStatus(ReportStatusEnum.REJECTED);
                report.setSolution(solution);
            } else {
                report.setReportStatus(ReportStatusEnum.PROCESSED);
                if (helperInformation != null) {
                    helperInformation.setLockDateExpired(Utils.getLocalDateTimeNow().toLocalDate().plusDays(2));
                }
                report.setRefund(reportRequest.getRefundPercent());
                report.setOption(OptionProcessReportEnum.MONEY);
                report.setProcessAt(Utils.getLocalDateTimeNow());
                solution = String.format(MessageVariable.REFUND_CANCEL_BOOKING,
                        report.getBookingDetail().getBooking().getBookingCode());
                report.setSolution(solution);
                reportResultResponse.setStatus(ReportStatusEnum.PROCESSED.name());
                reportResultResponse.setSolution(solution);
                reportResultResponse.setManagerName(manager.getFullName());
                if (renter.getEmail() != null && !renter.getEmail().isEmpty()) {
                    reportResultResponse.setTo(renter.getEmail());
                    reportResultResponse.setRenterName(renter.getFullName());
                    emailSenderService.sendEmailTemplate(SendMailOptionEnum.REPORT_RESULT, reportResultResponse);
                }
                Transaction transactionMoney = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(report.getBookingDetail().getBooking().getBookingId(),
                        WalletTypeEnum.MONEY, TransactionTypeEnum.WITHDRAW, report.getBookingDetail().getBooking().getRenter().getUserId());
                Transaction transactionPoint = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(report.getBookingDetail().getBooking().getBookingId(),
                        WalletTypeEnum.POINT, TransactionTypeEnum.WITHDRAW, report.getBookingDetail().getBooking().getRenter().getUserId());
                Double percent = 0D;
                double moneyRefund = 0D;
                double pointRefund = 0D;
                if(Objects.isNull(transactionPoint)){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                    MessageVariable.NOT_HAVE_TRANSACTION_POINT, null));
                }
                if(Objects.isNull(transactionMoney)){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                    MessageVariable.NOT_HAVE_TRANSACTION_MONEY, null));
                }
                if (report.getBookingDetail().getPriceDetail() < transactionMoney.getAmount()) {
                    percent = report.getBookingDetail().getPriceDetail() / transactionMoney.getAmount()
                            * reportRequest.getRefundPercent() / 100;
                } else {
                    percent = transactionMoney.getAmount() / report.getBookingDetail().getPriceDetail()
                            * reportRequest.getRefundPercent() / 100;
                }
                moneyRefund = transactionMoney.getAmount() * percent;
                pointRefund = transactionPoint.getAmount() * percent;
                if (moneyRefund > 0) {
                    createTransaction(new TransactionRequest(moneyRefund, String.format(MessageVariable.REFUND_CANCEL_BOOKING,
                            report.getBookingDetail().getBooking().getBookingCode()),
                            renter.getUserId(),
                            TransactionTypeEnum.DEPOSIT.name(),
                            WalletTypeEnum.MONEY.name()));
                }
                if (pointRefund > 0) {
                    createTransaction(new TransactionRequest(pointRefund, String.format(MessageVariable.REFUND_POINT_CANCEL_BOOKING,
                            report.getBookingDetail().getBooking().getBookingCode()),
                            renter.getUserId(),
                            TransactionTypeEnum.DEPOSIT.name(),
                            WalletTypeEnum.POINT.name()));
                }
            }
            Notification notification = new Notification();
            notification.setUser(renter);
            notification.setContent(solution);
            notification.setTitle(MessageVariable.TITLE_APP);
            notificationRepository.save(notification);
            reportRepository.save(report);
            bookingDetailRepository.save(bookingDetail);
            transactionManager.commit(status);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Report Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            transactionManager.rollback(status);
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    private void sendMessage(NotificationRequestDto notificationRequestDto, User user) {
        List<DeviceToken> deviceTokens = user.getDeviceTokens();
        if (!deviceTokens.isEmpty()) {
            notificationRequestDto.setTarget(convertToListFcmToken(deviceTokens));
            fcmService.sendPnsToTopic(notificationRequestDto);
        }
    }

    private List<String> convertToListFcmToken(List<DeviceToken> deviceTokens) {
        return deviceTokens.stream()
                .map(DeviceToken::getFcmToken)
                .collect(Collectors.toList());
    }

    public boolean createTransaction(TransactionRequest request) {
        User user = findById(request.getUserId());
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
        Wallet wallet = walletRepository.getWalletByUserIdAndType(request.getUserId(),
                WalletTypeEnum.valueOf(request.getWalletType().toUpperCase()));
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(0D);
            wallet.setWalletTypeEnum(WalletTypeEnum.valueOf(request.getWalletType().toUpperCase()));
        }

        wallet.setUpdateAt(Utils.getLocalDateTimeNow());
        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.valueOf(request.getTransactionType().toUpperCase());
        switch (transactionTypeEnum) {
            case DEPOSIT:
                wallet.setBalance(wallet.getBalance() + request.getBalance());
                notificationRequestDto.setBody(request.getNote());
                sendMessage(notificationRequestDto, user);
                break;
            case TRANSFER:
                break;
            case WITHDRAW:
                if (wallet.getBalance() < request.getBalance()) {
                    return false;
                }
                wallet.setBalance(wallet.getBalance() - request.getBalance());
                notificationRequestDto.setBody(request.getNote());
                sendMessage(notificationRequestDto, user);
                break;
        }
        Wallet walletUpdate = walletRepository.save(wallet);
        Transaction transaction = mappingForCreate(request);
        transaction.setWallet(walletUpdate);
        transactionRepository.save(transaction);
        return true;
    }

    private Transaction mappingForCreate(TransactionRequest request) {
        Transaction transaction = modelMapper.map(request, Transaction.class);
        transaction.setAmount(request.getBalance());
        transaction.setTransactionCode(Utils.generateRandomCode());
        transaction.setCreateAt(Utils.getLocalDateTimeNow());
        transaction.setTransactionStatusEnum(TransactionStatusEnum.SUCCESS);
        transaction.setTransactionTypeEnum(TransactionTypeEnum.valueOf(request.getTransactionType().toUpperCase()));
        return transaction;
    }


    @Override
    public ResponseEntity<ResponseObject> deleteReport(int reportId) {
        try {
            Report report = findReport(reportId);
            report.setReportStatus(ReportStatusEnum.REJECTED);
            reportRepository.save(report);
            BookingDetail bookingDetail = report.getBookingDetail();
            bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.FINISHED);
            bookingDetailRepository.save(bookingDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString(),
                            "Delete Report Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private Report mappingReportForCreate(CreateReportRequest request) {
        BookingDetail optionalBooking = findBookingDetail(request.getBookingDetailId());
        ReportType optionalReportType = findReportType(request.getReportTypeId());
        Report report = modelMapper.map(request, Report.class);
        report.setReportId(null);
        report.setDetail(request.getDetail());
        report.setReportStatus(ReportStatusEnum.PROCESSING);
        report.setCreateAt(Utils.getLocalDateTimeNow());
        report.setBookingDetail(optionalBooking);
        report.setReportType(optionalReportType);

        return report;
    }

    private Report findReport(int reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report is not exist"));
    }

    private ReportType findReportType(int reportTypeId) {
        return reportTypeRepository.findById(reportTypeId)
                .orElseThrow(() -> new NotFoundException("Report type is not exist"));
    }

    private BookingDetail findBookingDetail(int bookingDetailId) {
        return bookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }
}
