package iclean.code.function.booking.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.config.MessageVariable;
import iclean.code.config.SystemParameterField;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.Position;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.booking.*;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.booking.GetBookingResponseForHelper;
import iclean.code.data.dto.response.booking.GetCartResponseDetail;
import iclean.code.data.dto.response.booking.GetDetailBookingResponse;
import iclean.code.data.enumjava.*;
import iclean.code.data.repository.*;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.booking.service.BookingService;
import iclean.code.function.serviceprice.service.ServicePriceService;
import iclean.code.service.FCMService;
import iclean.code.service.GoogleMapService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BookingServiceImpl implements BookingService {

    @Autowired
    private FCMService fcmService;
    @Value("${iclean.app.max.distance.length}")
    private Double maxDistance;
    @Value("${iclean.app.point.to.money}")
    private Double pointToMoney;
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
    private ServiceUnitRepository serviceUnitRepository;
    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private DeviceTokenRepository deviceTokenRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private SystemParameterRepository systemParameterRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private BookingDetailHelperRepository bookingDetailHelperRepository;
    @Autowired
    private ServicePriceService servicePriceService;
    @Autowired
    GoogleMapService googleMapService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable, String status, Boolean isHelper) {
        try {
            Page<Booking> bookings = Page.empty();
            BookingStatusEnum bookingStatusEnum = null;
            if (!Utils.isNullOrEmpty(status)) {
                bookingStatusEnum = BookingStatusEnum.valueOf(status.toUpperCase());
            }
            String roleUser = userRepository.findByUserId(userId).getRole().getTitle().toUpperCase();
            if (Utils.isNullOrEmpty(roleUser))
                throw new UserNotHavePermissionException("User do not have permission to do this action");
            RoleEnum roleEnum = RoleEnum.valueOf(roleUser);
            switch (roleEnum) {
                case EMPLOYEE:
                    if (isHelper) {
                        bookings = bookingRepository.findByHelperId(userId, bookingStatusEnum, BookingStatusEnum.ON_CART, pageable);
                    } else {
                        bookings = bookingRepository.findByRenterId(userId, bookingStatusEnum, BookingStatusEnum.ON_CART, pageable);
                    }
                    break;
                case RENTER:
                    bookings = bookingRepository.findByRenterId(userId, bookingStatusEnum, BookingStatusEnum.ON_CART, pageable);
                    break;
                case MANAGER:
                case ADMIN:
                    if (!Utils.isNullOrEmpty(status)) {
                        bookings = bookingRepository.findAllByBookingStatus(bookingStatusEnum, BookingStatusEnum.ON_CART, pageable);
                    } else {
                        bookings = bookingRepository.findAllBooking(BookingStatusEnum.ON_CART, pageable);
                    }
                    break;
                default:
                    throw new UserNotHavePermissionException("User do not have permission to do this action");
            }
            PageResponseObject pageResponseObject = getResponseObjectResponseEntity(bookings);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Booking History Response!", pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getBookingsAround(Integer userId) {
        try {
            Address address;
            List<Address> addresses = addressRepository.findByUserIdAnAndIsDefault(userId);
            if (!addresses.isEmpty()) {
                address = addresses.get(0);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                MessageVariable.NEED_ADD_LOCATION, null));
            }
            User user = findAccount(userId);
            RoleEnum roleUser = RoleEnum.valueOf(user.getRole().getTitle().toUpperCase());
            List<BookingDetail> bookings;
            if (roleUser == RoleEnum.EMPLOYEE) {
                bookings = bookingDetailRepository.findBookingDetailByStatusAndNoUserIdNoEmployee(BookingStatusEnum.APPROVED, userId);
            } else {
                throw new UserNotHavePermissionException("User not have permission to do this action");
            }
            List<GetBookingResponseForHelper> dtoList = bookings
                    .stream()
                    .map(booking -> {
                                GetBookingResponseForHelper response = modelMapper.map(booking, GetBookingResponseForHelper.class);
                                response.setLatitude(booking.getBooking().getLatitude());
                                response.setLongitude(booking.getBooking().getLongitude());
                                response.setRenterName(booking.getBooking().getRenter().getFullName());
                                response.setServiceName(booking.getServiceUnit().getService().getServiceName());
                                response.setServiceImages(booking.getServiceUnit().getService().getServiceImage());
                                response.setAmount(booking.getPriceHelper());
                                response.setLocationDescription(booking.getBooking().getLocationDescription());
                                return response;
                            }
                    )
                    .collect(Collectors.toList());
            List<GetBookingResponseForHelper> response = null;
            if (address != null) {
                response = googleMapService.checkDistance(dtoList,
                        new Position(address.getLongitude(), address.getLatitude()), getMaxDistance());
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Booking History Response!", response));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getBookingDetailById(Integer bookingId, Integer userId) {
        try {
            Booking booking = findBookingById(bookingId);
            String roleUser = userRepository.findByUserId(userId).getRole().getTitle().toUpperCase();
            if (Utils.isNullOrEmpty(roleUser))
                throw new UserNotHavePermissionException("User do not have permission to do this action");
            RoleEnum roleEnum = RoleEnum.valueOf(roleUser);
            switch (roleEnum) {
                case RENTER:
                    isPermission(userId, booking);
                    break;
                case EMPLOYEE:
                    isPermissionHelper(userId, booking);
                case MANAGER:
                case ADMIN:
                    break;
                default:
                    throw new UserNotHavePermissionException("User do not have permission to do this action");
            }

            GetDetailBookingResponse response = modelMapper.map(booking, GetDetailBookingResponse.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Booking detail", response));
        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    private ServiceUnit findServiceUnitById(Integer id) {
        return serviceUnitRepository.findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Service Unit %s ID is not exist!", id)));
    }

    @Override
    public ResponseEntity<ResponseObject> createServiceToCart(AddBookingRequest request,
                                                              Integer userId) {
        try {
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            BookingStatusHistory bookingStatusHistory = null;
            Double priceDetail = servicePriceService
                    .getServicePrice(new GetServicePriceRequest(request.getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            Double priceHelper = servicePriceService
                    .getServiceHelperPrice(new GetServicePriceRequest(request.getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            if (booking == null) {
                bookingStatusHistory = new BookingStatusHistory();
                booking = new Booking();
                booking.setRenter(findAccount(userId));
                booking.setTotalPrice(0.0);
                booking.setBookingStatus(BookingStatusEnum.ON_CART);
                bookingStatusHistory.setBookingStatus(BookingStatusEnum.ON_CART);
            }
            double price;
            BookingDetail bookingDetail = new BookingDetail();
            ServiceUnit requestServiceUnit = findServiceUnitById(request.getServiceUnitId());
            Optional<BookingDetail> checkCurrentDetail = bookingDetailRepository
                    .findByServiceIdAndBookingStatus(requestServiceUnit.getService().getServiceId(), BookingStatusEnum.ON_CART);
            if (checkCurrentDetail.isPresent()) {
                bookingDetail = checkCurrentDetail.get();
                bookingDetail.setServiceUnit(requestServiceUnit);
                price = booking.getTotalPrice() - bookingDetail.getPriceDetail() + priceDetail;
            } else {
                bookingDetail.setServiceUnit(requestServiceUnit);
                bookingDetail.setNote(request.getNote());
                price = booking.getTotalPrice() + priceDetail;
            }
            booking.setTotalPrice(price);
            booking.setTotalPriceActual(price);
            bookingRepository.save(booking);
            if (bookingStatusHistory != null) {
                bookingStatusHistory.setBooking(booking);
                bookingStatusHistoryRepository.save(bookingStatusHistory);
            }

            bookingDetail.setBooking(booking);
            bookingDetail.setPriceDetail(priceDetail);
            bookingDetail.setPriceHelper(priceHelper);
            bookingDetail.setWorkStart(request.getStartTime().toLocalTime());
            bookingDetail.setWorkDate(request.getStartTime().toLocalDate());
            bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.ON_CART);
            bookingDetail.setNote(request.getNote());

            bookingDetailRepository.save(bookingDetail);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Booking Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getCart(Integer userId, Boolean usingPoint) {
        try {
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            if (booking != null && !booking.getBookingDetails().isEmpty()) {
                if (usingPoint) {
                    Wallet walletPoint = walletRepository.getWalletByUserIdAndType(userId, WalletTypeEnum.POINT);
                    Double minusMoney = walletPoint.getBalance() * getPointToMoney();
                    if (minusMoney > booking.getTotalPrice()) {
                        booking.setTotalPriceActual(0D);
                    } else {
                        booking.setTotalPriceActual(Utils.roundingNumber(minusMoney, "#", RoundingMode.DOWN));
                    }
                }
                GetCartResponseDetail responseDetail = modelMapper.map(booking, GetCartResponseDetail.class);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Get Cart Successfully!", responseDetail));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Get Cart Successfully!", null));

        } catch (Exception e) {
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
    public ResponseEntity<ResponseObject> deleteAllOnCart(Integer userId) {
        try {
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            bookingStatusHistoryRepository.deleteAll(booking.getBookingStatusHistories());
            bookingDetailRepository.deleteAll(booking.getBookingDetails());
            bookingRepository.delete(booking);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Cart Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
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

    private void isPermission(Integer userId, BookingDetail booking) throws UserNotHavePermissionException {
        if (!Objects.equals(booking.getBooking().getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
    }

    private void isPermission(Integer userId, Booking booking) throws UserNotHavePermissionException {
        if (!Objects.equals(booking.getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
    }

    private void isPermissionHelper(Integer userId, Booking booking) throws UserNotHavePermissionException {
        switch (booking.getBookingStatus()) {
            case APPROVED:
            case FINISHED:
            case IN_PROCESSING:
            case WAITING:
            case EMPLOYEE_ACCEPTED:
            case EMPLOYEE_CANCELED:
            case RENTER_CANCELED:
                break;
            default:
                throw new UserNotHavePermissionException("User do not have permission to do this action");
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteServiceOnCart(Integer userId, Integer detailId) {
        try {
            Optional<BookingDetail> bookingDetail = bookingDetailRepository
                    .findByBookingDetailIdAndBookingStatus(detailId, BookingStatusEnum.ON_CART);
            if (bookingDetail.isPresent()) {
                Booking booking = bookingDetail.get().getBooking();
                Double totalPrice = booking.getTotalPrice() - bookingDetail.get().getPriceDetail();
                booking.setTotalPriceActual(totalPrice);
                booking.setTotalPrice(totalPrice);
                isPermission(userId, bookingDetail.get());
                bookingRepository.save(booking);
                bookingDetailRepository.delete(bookingDetail.get());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Delete a service on cart successful", null));
            }
            throw new NotFoundException(String.format("The service with detail ID: %s is not on this cart!", detailId));

        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
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

    private Address findAddressById(Integer id) {
        return addressRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Address ID %s is not found", id)));
    }

    @Override
    public ResponseEntity<ResponseObject> checkoutCart(Integer userId, CheckOutCartRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            Address address = findAddressById(request.getAddressId());
            if (!Objects.equals(address.getUser().getUserId(), userId)) {
                throw new UserNotHavePermissionException("Address is not available!");
            }
            User renter = findAccount(userId);
            booking.setLongitude(address.getLongitude());
            booking.setLatitude(address.getLatitude());
            booking.setLocation(address.getLocationName());
            booking.setLocationDescription(address.getDescription());
            booking.setRequestCount(1);
            booking.setBookingStatus(BookingStatusEnum.NOT_YET);
            booking.setUpdateAt(Utils.getLocalDateTimeNow());
            booking.setBookingCode(Utils.generateRandomCode());
            booking.setUsingPoint(request.getUsingPoint());
            booking.setOption(objectMapper.writeValueAsString(request.getAutoAssign()));
            if (request.getUsingPoint()) {
                Wallet walletPoint = walletRepository.getWalletByUserIdAndType(userId, WalletTypeEnum.POINT);
                Double minusMoney = walletPoint.getBalance() * getPointToMoney();
                Double usingPoint;
                if (minusMoney > booking.getTotalPrice()) {
                    booking.setTotalPriceActual(0D);
                    usingPoint = (minusMoney - booking.getTotalPrice()) / getPointToMoney();
                    usingPoint = Utils.roundingNumber(usingPoint, "#", RoundingMode.DOWN);
                } else {
                    booking.setTotalPriceActual(Utils.roundingNumber(minusMoney, "#", RoundingMode.DOWN));
                    usingPoint = walletPoint.getBalance();
                }
                createTransaction(new TransactionRequest(walletPoint.getBalance() - usingPoint,
                        MessageVariable.USING_POINT_FOR_BOOKING,
                        renter.getUserId(),
                        TransactionTypeEnum.WITHDRAW.name(),
                        WalletTypeEnum.POINT.name(),
                        booking.getBookingId())
                );
            }

            BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
            bookingStatusHistory.setBookingStatus(BookingStatusEnum.NOT_YET);
            bookingStatusHistory.setBooking(booking);
            bookingStatusHistoryRepository.save(bookingStatusHistory);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
            notificationRequestDto.setBody(String.format(MessageVariable.ORDER_SUCCESSFUL, booking.getBookingId()));
            sendMessage(notificationRequestDto, findAccount(userId));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Checkout Successfully!", null));

        } catch (Exception e) {
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

    private List<String> convertToListFcmToken(List<DeviceToken> deviceTokens) {
        return deviceTokens.stream()
                .map(DeviceToken::getFcmToken)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<ResponseObject> acceptBookingForHelper(CreateBookingHelperRequest request, Integer userId) {
        try {
            Booking booking = bookingRepository.findBookingByBookingDetailAndStatus(request.getBookingDetailId(), BookingStatusEnum.APPROVED);
            if (booking == null) throw new NotFoundException("The booking cannot do this action");
            BookingDetail bookingDetail = findBookingDetail(request.getBookingDetailId());
            if (Objects.equals(bookingDetail.getBooking().getRenter().getUserId(), userId)) {
                throw new UserNotHavePermissionException("Cannot accept your booking!");
            }
            ServiceRegistration serviceRegistration = serviceRegistrationRepository
                    .findByServiceIdAndUserId(bookingDetail.getServiceUnit().getService().getServiceId(), userId,
                            ServiceHelperStatusEnum.ACTIVE);
            if (serviceRegistration == null) {
                throw new BadRequestException("You dont have permission to do this service");
            }
            // if the first helper accept booking notification to renter
            if (bookingDetail.getBookingDetailHelpers().size() == 0) {
                NotificationRequestDto notification = new NotificationRequestDto();
                notification.setTarget(booking.getRenter().getDeviceTokens()
                        .stream()
                        .map(DeviceToken::getFcmToken)
                        .collect(Collectors.toList()));
                notification.setTitle(String.format(MessageVariable.TITLE_APP));
                notification.setBody(String.format(MessageVariable.ORDER_HAVE_HELPER,
                        bookingDetail.getServiceUnit().getService().getServiceName(),
                        booking.getBookingCode()));
                if (!notification.getTarget().isEmpty()) {
                    fcmService.sendPnsToTopic(notification);
                }
            }
            BookingDetailHelper bookingDetailHelper = new BookingDetailHelper();
            bookingDetailHelper.setBookingDetail(bookingDetail);
            bookingDetailHelper.setServiceRegistration(serviceRegistration);
            bookingDetailHelper.setBookingDetailHelperStatus(BookingDetailHelperStatusEnum.INACTIVE);
            bookingDetailHelperRepository.save(bookingDetailHelper);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Accept a booking successful", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> acceptOrRejectBooking(Integer bookingId, AcceptRejectBookingRequest request, Integer managerId) {
        try {
            Booking booking = findBookingById(bookingId);
            User renter = booking.getRenter();
            booking.setManager(userRepository.findByUserId(managerId));
            BookingStatusEnum bookingStatusEnum = BookingStatusEnum.valueOf(request.getAction().toUpperCase());
            switch (bookingStatusEnum) {
                case REJECTED:
                    if (Utils.isNullOrEmpty(request.getRejectionReasonDetail())) {
                        throw new BadRequestException("Rejection Reason are required");
                    }
                    booking.setRejectionReason(findById(request.getRejectionReasonId()));
                    booking.setRjReasonDescription(request.getRejectionReasonDetail().trim());
                    break;
                case APPROVED:
                    break;
                default:
                    throw new UserNotHavePermissionException("Cannot update to this status");
            }
            boolean checkMoney = createTransaction(new TransactionRequest(booking.getTotalPriceActual(),
                    MessageVariable.PAYMENT_SUCCESS,
                    renter.getUserId(),
                    TransactionTypeEnum.WITHDRAW.name(),
                    WalletTypeEnum.MONEY.name(),
                    bookingId));
            if (!checkMoney) {
                bookingStatusEnum = BookingStatusEnum.NO_MONEY;
                NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                notificationRequestDto.setBody(MessageVariable.PAYMENT_NO_MONEY);
                sendMessage(notificationRequestDto, renter);
            } else {
                booking.setBookingStatus(bookingStatusEnum);
                booking.setUpdateAt(Utils.getLocalDateTimeNow());
            }
            BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
            bookingStatusHistory.setBookingStatus(bookingStatusEnum);
            bookingStatusHistory.setBooking(booking);
            booking.setBookingStatus(bookingStatusEnum);
            bookingStatusHistoryRepository.save(bookingStatusHistory);
            bookingRepository.save(booking);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Accept/Reject a booking successful", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof BadRequestException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> paymentBooking(Integer bookingId, Integer renterId, PaymentBookingRequest request) {
        try {
            Booking booking = findBookingById(bookingId);
            isPermission(renterId, booking);
            if (booking.getBookingStatus() != BookingStatusEnum.NO_MONEY)
                throw new BadRequestException("The booking cannot make a payment!");
            Wallet walletMoney = walletRepository.getWalletByUserIdAndType(renterId, WalletTypeEnum.MONEY);

            boolean checkMoney = createTransaction(new TransactionRequest(walletMoney.getBalance() - booking.getTotalPriceActual(),
                            MessageVariable.USING_MONEY_FOR_BOOKING,
                            renterId,
                            TransactionTypeEnum.WITHDRAW.name(),
                            WalletTypeEnum.MONEY.name(),
                            booking.getBookingId()));
            if (checkMoney) {
                booking.setBookingStatus(BookingStatusEnum.APPROVED);
                BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
                bookingStatusHistory.setBooking(booking);
                bookingStatusHistory.setBookingStatus(BookingStatusEnum.APPROVED);
                bookingStatusHistoryRepository.save(bookingStatusHistory);
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                MessageVariable.PAYMENT_NO_MONEY, null));
            }
            bookingRepository.save(booking);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Make a payment booking successful", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
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

    public boolean createTransaction(TransactionRequest request) {
        User user = findAccount(request.getUserId());
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

    private PageResponseObject getResponseObjectResponseEntity(Page<Booking> bookings) {
        List<GetBookingResponse> dtoList = bookings
                .stream()
                .map(booking -> {
                            GetBookingResponse response = modelMapper.map(booking, GetBookingResponse.class);
                            List<iclean.code.data.domain.Service> services = booking.getBookingDetails()
                                    .stream()
                                    .map(detail -> detail.getServiceUnit()
                                            .getService()).collect(Collectors.toList());
                            if (!services.isEmpty()) {
                                response.setServiceNames(services
                                        .stream()
                                        .map(iclean.code.data.domain.Service::getServiceName)
                                        .collect(Collectors.joining(",")));
                                response.setServiceAvatar(services.get(0).getServiceImage());
                            } else {
                                response.setServiceNames(null);
                            }
                            if (booking.getBookingStatus() != null) {
                                response.setBookingStatus(booking.getBookingStatus().name());
                            }
                            return response;
                        }
                )
                .collect(Collectors.toList());
        return Utils.convertToPageResponse(bookings, Collections.singletonList(dtoList));
    }

    private User findAccount(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    private RejectionReason findById(int id) {
        return rejectionReasonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rejection Reason is not exist"));
    }

    private BookingDetail findBookingDetail(int id) {
        return bookingDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking Detail ID %s is not exist!", id)));
    }

    private Booking findBookingById(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    private Double getPointToMoney() {
        SystemParameter systemParameter = systemParameterRepository.findSystemParameterByParameterField(SystemParameterField.POINT_TO_MONEY);
        try {
            return Double.parseDouble(systemParameter.getParameterValue());
        } catch (Exception e) {
            return pointToMoney;
        }
    }

    private Double getMaxDistance() {
        SystemParameter systemParameter = systemParameterRepository.findSystemParameterByParameterField(SystemParameterField.MAX_DISTANCE);
        try {
            return Double.parseDouble(systemParameter.getParameterValue());
        } catch (Exception e) {
            return maxDistance;
        }
    }
}
