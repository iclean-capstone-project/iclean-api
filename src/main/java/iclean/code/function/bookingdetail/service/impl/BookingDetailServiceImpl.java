package iclean.code.function.bookingdetail.service.impl;

import iclean.code.config.MessageVariable;
import iclean.code.config.SystemParameterField;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.booking.QRCodeValidate;
import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.response.bookingdetail.QRCodeResponse;
import iclean.code.data.dto.response.bookingdetail.UpdateBookingDetailRequest;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import iclean.code.data.enumjava.BookingStatusEnum;
import iclean.code.data.repository.*;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.bookingdetail.service.BookingDetailService;
import iclean.code.function.serviceprice.service.ServicePriceService;
import iclean.code.service.FCMService;
import iclean.code.service.GoogleMapService;
import iclean.code.service.QRCodeService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BookingDetailServiceImpl implements BookingDetailService {
    @Autowired
    private FCMService fcmService;

    @Value("${iclean.app.max.distance.length}")
    private Double maxDistance;

    @Value("${iclean.app.max.late.minutes}")
    private long maxLateMinutes;
    @Value("${iclean.app.max.soon.minutes}")
    private long maxSoonMinutes;

    @Value("${iclean.app.max.update.and.cancel.minutes}")
    private long maxUpdateHour;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRegistrationRepository serviceRegistrationRepository;

    @Autowired
    private BookingStatusHistoryRepository bookingStatusHistoryRepository;

    @Autowired
    private BookingEmployeeRepository bookingEmployeeRepository;

    @Autowired
    private ServiceUnitRepository serviceUnitRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired
    private  BookingDetailHelperRepository bookingDetailHelperRepository;

    @Autowired
    private SystemParameterRepository systemParameterRepository;

    @Autowired
    private ServicePriceService servicePriceService;

    @Autowired
    GoogleMapService googleMapService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> cancelBookingDetail(Integer renterId, Integer detailId) {
        try {
            BookingDetail bookingDetail = findById(detailId);
            Booking booking = bookingDetail.getBooking();
            List<Integer> userIds;
            List<BookingDetailHelper> bookingDetailHelpers = new ArrayList<>();
            switch (booking.getBookingStatus()) {
                case EMPLOYEE_ACCEPTED:
                    bookingDetailHelpers = bookingDetailHelperRepository.findByBookingDetailId(detailId);
                case WAITING:
                    String checkTime = checkInTime(LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart()));
                    if (!Utils.isNullOrEmpty(checkTime)) {
                        throw new BadRequestException(String.format(checkTime, bookingDetail.getServiceUnit().getService().getServiceName()));
                    }
                    if (booking.getBookingStatus() == BookingStatusEnum.WAITING) {
                        bookingDetailHelpers = bookingDetailHelperRepository.findByBookingDetailIdAndActive(detailId, BookingDetailHelperStatusEnum.ACTIVE);
                    }
                    userIds = bookingDetailHelpers
                            .stream()
                            .map(element -> element
                                    .getServiceRegistration()
                                    .getHelperInformation()
                                    .getUser().getUserId()).collect(Collectors.toList());
                    NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                    notificationRequestDto.setBody(MessageVariable.RENTER_CANCEL_BOOKING);
                    notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                    sendNotificationForUser(notificationRequestDto, userIds);
                case APPROVED:
                case NOT_YET:
                    bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.CANCEL_BY_RENTER);
                    bookingDetailRepository.save(bookingDetail);
                    List<BookingDetail> bookingDetails = booking.getBookingDetails()
                            .stream()
                            .filter(detail -> detail.getBookingDetailStatusEnum() == BookingDetailStatusEnum.CANCEL_BY_RENTER ||
                                    detail.getBookingDetailStatusEnum() == BookingDetailStatusEnum.CANCEL_BY_HELPER)
                            .collect(Collectors.toList());
                    if (bookingDetails.size() + 1 == booking.getBookingDetails().size()) {
                        BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
                        bookingStatusHistory.setBooking(booking);
                        bookingStatusHistory.setBookingStatus(BookingStatusEnum.RENTER_CANCELED);
                        bookingStatusHistoryRepository.save(bookingStatusHistory);
                        booking.setBookingStatus(BookingStatusEnum.RENTER_CANCELED);
                    }
                    break;
                default:
                    throw new BadRequestException(MessageVariable.CANNOT_CANCEL_BOOKING);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Cancel a service of a booking successful!", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> cancelBookingDetailByHelper(Integer helperId, Integer detailId) {
        try {
            BookingDetail bookingDetail = findById(detailId);
            findByHelperId(helperId);
            Booking booking = bookingDetail.getBooking();
            switch (booking.getBookingStatus()) {
                case EMPLOYEE_ACCEPTED:
                case WAITING:
                    String checkTime = checkInTime(LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart()));
                    if (!Utils.isNullOrEmpty(checkTime)) {
                        throw new BadRequestException(String.format(checkTime, bookingDetail.getServiceUnit().getService().getServiceName()));
                    }
                    NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                    notificationRequestDto.setBody(MessageVariable.RENTER_CANCEL_BOOKING);
                    notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                    sendNotificationForUser(notificationRequestDto, booking.getRenter().getUserId());
                case APPROVED:
                    bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.CANCEL_BY_HELPER);
                    bookingDetailRepository.save(bookingDetail);
                    List<BookingDetail> bookingDetails = booking.getBookingDetails()
                            .stream()
                            .filter(detail -> detail.getBookingDetailStatusEnum() == BookingDetailStatusEnum.CANCEL_BY_RENTER ||
                                    detail.getBookingDetailStatusEnum() == BookingDetailStatusEnum.CANCEL_BY_HELPER)
                            .collect(Collectors.toList());
                    if (bookingDetails.size() + 1 == booking.getBookingDetails().size()) {
                        BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
                        bookingStatusHistory.setBooking(booking);
                        bookingStatusHistory.setBookingStatus(BookingStatusEnum.EMPLOYEE_CANCELED);
                        bookingStatusHistoryRepository.save(bookingStatusHistory);
                        booking.setBookingStatus(BookingStatusEnum.EMPLOYEE_CANCELED);
                    }
                    break;
                case REJECTED:
                case RENTER_CANCELED:
                case NOT_YET:
                    break;
                default:
                    throw new BadRequestException(MessageVariable.CANNOT_CANCEL_BOOKING);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Cancel a service of a booking successful!", null));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateBookingDetail(int detailId, Integer renterId, UpdateBookingDetailRequest request) {
        try {
            BookingDetail bookingDetail = findBookingDetail(detailId);
            Booking booking = bookingDetail.getBooking();
            Double priceDetail = servicePriceService
                    .getServicePrice(new GetServicePriceRequest(bookingDetail.getServiceUnit().getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            Double priceHelper = servicePriceService
                    .getServiceHelperPrice(new GetServicePriceRequest(bookingDetail.getServiceUnit().getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            switch (bookingDetail.getBooking().getBookingStatus()) {
                case WAITING:
                    long difference = Utils.minusLocalDateTime(Utils.getLocalDateTimeNow(),
                            LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart()));
                    if (Utils.isSoonMinutes(difference, getMaxUpdateHour())) {
                        throw new BadRequestException("Its too late to update the booking!");
                    }
                    BookingDetailHelper bookingDetailHelper = findBookingDetailHelperByBookingDetailId(
                            bookingDetail.getBookingDetailId());
                    NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                    notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                    notificationRequestDto.setBody(MessageVariable.RENTER_CHANGE_BOOKING);
                    sendNotificationForUser(notificationRequestDto, bookingDetailHelper
                            .getServiceRegistration()
                            .getHelperInformation().getUser().getUserId());
                case ON_CART:
                case APPROVED:
                case NOT_YET:
                case REJECTED:
                case EMPLOYEE_ACCEPTED:
                case EMPLOYEE_CANCELED:
                case RENTER_CANCELED:
                    bookingDetail.setNote(request.getNote());
                    bookingDetail.setWorkDate(request.getStartTime().toLocalDate());
                    bookingDetail.setWorkStart(request.getStartTime().toLocalTime());
                    break;
                default:
                    throw new BadRequestException("Cannot update a this booking!");
            }
            double price;
            price = booking.getTotalPrice() - bookingDetail.getPriceDetail();
            booking.setTotalPrice(price);
            booking.setTotalPriceActual(price);
            bookingRepository.save(booking);

            bookingDetail.setPriceDetail(priceDetail);
            bookingDetail.setPriceHelper(priceHelper);
            bookingDetailRepository.save(bookingDetail);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Booking Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }
    @Override
    public ResponseEntity<ResponseObject> validateBookingToStart(Integer userId, Integer detailId, QRCodeValidate request) {
        try {
            BookingDetail bookingDetail = findBookingDetail(detailId);
            isPermissionForHelper(userId, bookingDetail);
            ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest(request.getQrCode(), bookingDetail.getQrCode());
            boolean check = qrCodeService.validateQRCode(validateOTPRequest);
            String onTimeToWork = checkInTime(LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart()));
            if (!Utils.isNullOrEmpty(onTimeToWork) &&
                    MessageVariable.TOO_LATE_TO_START.equals(onTimeToWork)) {
                bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.CANCEL_BY_SYSTEM);
            }
            if (!Utils.isNullOrEmpty(onTimeToWork)) {
                throw new BadRequestException(onTimeToWork);
            }
            if (check) {
                bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.IN_PROCESS);
                bookingDetail.setQrCode(null);
                bookingDetailRepository.save(bookingDetail);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Validate booking successful, helper can start to do this service", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                "Invalid QR Code, cannot do this service", null));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof BadRequestException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }
    @Override
    public ResponseEntity<ResponseObject> generateQrCode(Integer renterId, Integer detailId) {
        try {
            BookingDetail bookingDetail = findBookingDetailByStatus(detailId, BookingStatusEnum.WAITING);
            isPermission(renterId, bookingDetail);
            String onTimeToWork = checkInTime(LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart()));
            if (!Utils.isNullOrEmpty(onTimeToWork) &&
                    MessageVariable.TOO_LATE_TO_START.equals(onTimeToWork)) {
                bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.CANCEL_BY_SYSTEM);
            }
            if (!Utils.isNullOrEmpty(onTimeToWork)) {
                throw new BadRequestException(onTimeToWork);
            }
            String qrCode = qrCodeService.generateCodeValue();
            QRCodeResponse response = new QRCodeResponse();
            response.setValue(qrCode);
            String hashQrCode = qrCodeService.hashQrCode(qrCode);
            bookingDetail.setQrCode(hashQrCode);
            bookingDetailRepository.save(bookingDetail);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Generate qr code successful!", response));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof BadRequestException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }


    private void sendNotificationForUser(NotificationRequestDto request, Integer userId) {
        User user = findAccount(userId);
        request.setTarget(user.getDeviceTokens()
                .stream()
                .map(DeviceToken::getFcmToken)
                .collect(Collectors.toList()));
        if (!request.getTarget().isEmpty()) {
            fcmService.sendPnsToTopic(request);
        }
    }
    private void isPermissionForHelper(Integer userId, BookingDetail bookingDetail) throws UserNotHavePermissionException {
        List<BookingDetailHelper> helpers = bookingDetailHelperRepository.findByBookingDetailIdAndActive(bookingDetail.getBookingDetailId(),
                BookingDetailHelperStatusEnum.ACTIVE);
        User helper = helpers.get(0).getServiceRegistration().getHelperInformation().getUser();
        if (!Objects.equals(helper.getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
    }

    private void sendNotificationForUser(NotificationRequestDto request, List<Integer> userIds) {
        for (Integer userId :
                userIds) {
            User user = findAccount(userId);
            request.setTarget(user.getDeviceTokens()
                    .stream()
                    .map(DeviceToken::getFcmToken)
                    .collect(Collectors.toList()));
            if (!request.getTarget().isEmpty()) {
                fcmService.sendPnsToTopic(request);
            }
        }
    }
    private String checkInTime(LocalDateTime startDateTime) {
        LocalDateTime current = Utils.getLocalDateTimeNow();
        long difference = Utils.minusLocalDateTime(startDateTime,
                current);
        if (difference >= 0 && Utils.isLateMinutes(difference, getMaxUpdateHour())) {
            return MessageVariable.TOO_LATE_TO_UPDATE;
        }
        return null;
    }
    private boolean isPermission(Integer userId, BookingDetail booking) throws UserNotHavePermissionException {
        if (!Objects.equals(booking.getBooking().getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
        return true;
    }
    private BookingDetailHelper findBookingDetailHelperByBookingDetailId(Integer bookingDetailId) throws BadRequestException {
        List<BookingDetailHelper> data = bookingDetailHelperRepository.findByBookingDetailIdAndActive(bookingDetailId, BookingDetailHelperStatusEnum.ACTIVE);
        if (data.isEmpty()) {
            throw new NotFoundException("The booking not have helper yet!");
        }
        if (data.size() > 1) {
            throw new BadRequestException("The booking have more than 1 helper!");
        }
        return data.get(0);
    }
    private BookingDetailHelper findByHelperId(int id) throws UserNotHavePermissionException {
        return bookingDetailHelperRepository.findByHelperId(id).orElseThrow(() ->
                new UserNotHavePermissionException("Helper do not to have permission to action this booking!"));
    }
    private BookingDetail findById(int id) {
        return bookingDetailRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Booking detail is not exist!"));
    }
    private BookingDetail findBookingDetailByStatus(Integer detailId, BookingStatusEnum statusEnum) {
        return bookingDetailRepository.findByBookingDetailIdAndBookingStatus(detailId, statusEnum)
                .orElseThrow(() -> new NotFoundException("Booking Detail is not found"));
    }
    private long getMaxUpdateHour() {
        SystemParameter systemParameter = systemParameterRepository.findSystemParameterByParameterField(SystemParameterField.MAX_UPDATE_HOUR);
        try {
            return Long.parseLong(systemParameter.getParameterValue());
        } catch (Exception e) {
            return maxUpdateHour;
        }
    }
    private BookingDetail findBookingDetail(int id) {
        return bookingDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking Detail ID %s is not exist!", id)));
    }

    private User findAccount(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }
}
