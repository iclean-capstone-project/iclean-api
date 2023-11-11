package iclean.code.function.booking.automation;

import iclean.code.config.MessageVariable;
import iclean.code.data.domain.*;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.enumjava.*;
import iclean.code.data.repository.*;
import iclean.code.service.FCMService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AutoAddTransactionForFinishedBooking {

    @Autowired
    private BookingStatusHistoryRepository bookingStatusHistoryRepository;

    @Autowired
    private BookingDetailHelperRepository bookingDetailHelperRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired
    private FCMService fcmService;

    // Run the task every day at 8 AM
    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Bangkok")
    public void jobMapForManager() {
        try {
            List<BookingStatusHistory> bookingStatusHistories =
                    bookingStatusHistoryRepository.findBookingStatusHistoryFinishedAfterThreeDays(BookingStatusEnum.FINISHED);

            if (bookingStatusHistories.isEmpty()) {
                log.info(Utils.getDateTimeNowAsString() + " ----> No booking history have status FINISHED");
                return;
            }

            for (BookingStatusHistory bookingStatusHistory : bookingStatusHistories) {
                List<BookingDetailHelper> bookingDetailHelpers =
                        bookingDetailHelperRepository.findBookingDetailHelperHaveFinishedStatus(
                                bookingStatusHistory.getStatusHistoryId(),
                                BookingDetailStatusEnum.FINISHED);
                if (bookingDetailHelpers.isEmpty()) {
                    log.info(Utils.getDateTimeNowAsString() + " ----> No booking detail have status FINISHED");
                    return;
                }
                for (BookingDetailHelper bookingDetailHelper : bookingDetailHelpers) {
                    Integer userId = bookingDetailHelper.getServiceRegistration().getHelperInformation().getUser().getUserId();
                    Wallet wallet = walletRepository.getWalletByUserIdAndType(
                            userId,
                            WalletTypeEnum.MONEY);
                    if (Objects.isNull(wallet)){
                        log.info(Utils.getDateTimeNowAsString() + " ----> No booking detail have status FINISHED");
                        return;
                    }

                    //Add new Transaction record
                    Transaction transaction = new Transaction();

                    transaction.setBooking(bookingDetailHelper.getBookingDetail().getBooking());
                    transaction.setWallet(wallet);
                    transaction.setAmount(bookingDetailHelper.getBookingDetail().getPriceHelper());
                    transaction.setTransactionStatusEnum(TransactionStatusEnum.SUCCESS);
                    transaction.setTransactionTypeEnum(TransactionTypeEnum.TRANSFER);
                    transaction.setNote(String.format(MessageVariable.TRANSFER_SUCCESSFUL,
                            bookingDetailHelper.getServiceRegistration().getHelperInformation().getHelperInformationId()));
                    transaction.setCreateAt(Utils.getDateTimeNow());
                    transactionRepository.save(transaction);

                    //SEND NOTIFICATION
                    List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserId(userId);
                    if (!deviceTokens.isEmpty()) {
                        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                        notificationRequestDto.setTarget(convertToListFcmToken(deviceTokens));
                        notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                        notificationRequestDto.setBody(String.format(MessageVariable.TRANSFER_SUCCESSFUL,
                                bookingDetailHelper.getServiceRegistration().getHelperInformation().getHelperInformationId()));

                        fcmService.sendPnsToTopic(notificationRequestDto);
                    }
                }
            }


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    private List<String> convertToListFcmToken(List<DeviceToken> deviceTokens) {
        return deviceTokens.stream()
                .map(DeviceToken::getFcmToken)
                .collect(Collectors.toList());
    }
}
