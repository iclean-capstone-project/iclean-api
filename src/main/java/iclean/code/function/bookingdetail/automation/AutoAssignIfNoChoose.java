package iclean.code.function.bookingdetail.automation;

import iclean.code.config.MessageVariable;
import iclean.code.data.domain.*;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.data.dto.response.helperinformation.GetPriorityResponse;
import iclean.code.data.enumjava.*;
import iclean.code.data.repository.*;
import iclean.code.exception.NotFoundException;
import iclean.code.service.FCMService;
import iclean.code.service.GoogleMapService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AutoAssignIfNoChoose {
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private BookingDetailHelperRepository bookingDetailHelperRepository;
    @Autowired
    private ServiceRegistrationRepository serviceRegistrationRepository;
    @Autowired
    private HelperInformationRepository helperInformationRepository;
    @Autowired
    private FCMService fcmService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    GoogleMapService googleMapService;
    @Autowired
    private ModelMapper modelMapper;

    //1h
    @Scheduled(fixedRate = 3600000L)
    public void autoMapHelper() {
        try {
            LocalDateTime now = Utils.getLocalDateTimeNow();
            List<BookingDetail> bookingDetails = bookingDetailRepository.findAllByApprovedAndNoHelper(BookingDetailStatusEnum.APPROVED,
                    now.toLocalDate(), BookingDetailHelperStatusEnum.ACTIVE);
            List<BookingDetail> needAssignBookings = bookingDetails.stream().filter(bookingDetail ->
                            Utils.minusLocalTimeAsMinutes(bookingDetail.getWorkStart(), now.toLocalTime()) >= 15L && bookingDetail.getWorkStart().isAfter(now.toLocalTime()))
                    .collect(Collectors.toList());
            DayOfWeek currentDate = now.getDayOfWeek();

            List<BookingDetailHelper> needToAssigns = new ArrayList<>();
            for (BookingDetail bookingDetail :
                    needAssignBookings) {

                LocalDateTime startDateTime = LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart());
                LocalDateTime endDateTime = Utils.plusLocalDateTime(startDateTime, bookingDetail.getServiceUnit().getUnit().getUnitValue());

                // lấy tất cả các helper có workschedule thỏa điều kiện startDateTime và endDateTime và service đã đăng kí
                List<HelperInformation> helpersInformation = helperInformationRepository.findAllByWorkScheduleStartEndAndServiceId(startDateTime, endDateTime, currentDate,
                        bookingDetail.getServiceUnit().getService().getServiceId(), ServiceHelperStatusEnum.ACTIVE, BookingDetailHelperStatusEnum.ACTIVE);

                HelperInformation helperInformation = getPriority(helpersInformation, bookingDetail.getServiceUnit().getService().getServiceId());
                BookingDetailHelper bookingDetailHelper = new BookingDetailHelper();
                bookingDetailHelper.setBookingDetail(bookingDetail);
                bookingDetailHelper.setBookingDetailHelperStatus(BookingDetailHelperStatusEnum.ACTIVE);
                if (helperInformation != null) {
                    ServiceRegistration serviceRegistration = serviceRegistrationRepository.findByServiceIdAndUserId(bookingDetail.getServiceUnit().getService().getServiceId(),
                            helperInformation.getUser().getUserId());
                    bookingDetailHelper.setServiceRegistration(serviceRegistration);
                    needToAssigns.add(bookingDetailHelper);
                } else {
                }
            }
            bookingDetailHelperRepository.saveAll(needToAssigns);
            log.info(Utils.getDateTimeNowAsString() + " ----> Set Helper successful!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 3600000L)
    public void autoSendMoney() {
        try {
            List<BookingDetail> bookingDetails = bookingDetailRepository.findAllByBookingDetailStatus(BookingDetailStatusEnum.FINISHED);
            for (BookingDetail bookingDetail :
                    bookingDetails) {
                BookingDetailHelper bookingDetailHelper = bookingDetailHelperRepository.findByBookingDetailIdAndActiveLimit(bookingDetail.getBookingDetailId(),
                        BookingDetailHelperStatusEnum.ACTIVE);
                if (bookingDetailHelper == null) {
                    continue;
                }
                createTransaction(new TransactionRequest(
                        bookingDetail.getPriceHelper(),
                        String.format(MessageVariable.PAYMENT_FOR_HELPER, bookingDetail.getBooking().getBookingCode(),
                                bookingDetail.getServiceUnit().getService().getServiceName()),
                        bookingDetailHelper.getServiceRegistration().getHelperInformation().getUser().getUserId(),
                        TransactionTypeEnum.DEPOSIT.name(),
                        WalletTypeEnum.MONEY.name(),
                        bookingDetail.getBooking().getBookingId()
                ));
                bookingDetail.setPriceHelper(0D);
            }
            bookingDetailRepository.saveAll(bookingDetails);
            log.info(Utils.getDateTimeNowAsString() + " ----> Auto Send Money Successful!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private HelperInformation getPriority(List<HelperInformation> helpersInformation, Integer serviceId) {
        try {
            List<Integer> helperIds = helpersInformation.stream().map(HelperInformation::getHelperInformationId).collect(Collectors.toList());
            List<GetPriorityResponse> priorityResponses = new ArrayList<>();
            for (Integer helperId :
                    helperIds) {
                GetPriorityResponse priorityResponse = bookingDetailRepository.findPriority(BookingDetailStatusEnum.FINISHED, serviceId, helperId);
                priorityResponses.add(priorityResponse);
            }
            double maxValue = 0D;
            Integer helperId = helperIds.get(0);
            for (GetPriorityResponse response :
                    priorityResponses) {
                double check = response.getAvgRate() * 0.4 + (double) 5 / (response.getNumberOfBookingDetail() + 1) * 0.6;
                if (check > maxValue) {
                    maxValue = check;
                    helperId = response.getHelperInformationId();
                }
            }
            return helperInformationRepository.findById(helperId).orElseThrow(() -> new NotFoundException("Helper Not Found"));

        } catch (Exception e) {
            return null;
        }
    }

    private User findAccount(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    private Booking findBookingById(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    public boolean createTransaction(TransactionRequest request) {
        User user = findAccount(request.getUserId());
        Booking booking = findBookingById(request.getBookingId());
        if (request.getBookingId() != null) {
            request.setNote(String.format(request.getNote(), booking.getBookingCode()));
        }
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
        Booking booking = findBookingById(request.getBookingId());
        transaction.setAmount(request.getBalance());
        transaction.setBooking(booking);
        transaction.setTransactionCode(Utils.generateRandomCode());
        transaction.setCreateAt(Utils.getLocalDateTimeNow());
        transaction.setTransactionStatusEnum(TransactionStatusEnum.SUCCESS);
        transaction.setTransactionTypeEnum(TransactionTypeEnum.valueOf(request.getTransactionType().toUpperCase()));
        return transaction;
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
}
