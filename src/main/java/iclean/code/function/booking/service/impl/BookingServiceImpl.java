package iclean.code.function.booking.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.config.MessageVariable;
import iclean.code.config.SystemParameterField;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.booking.*;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.booking.GetCartResponseDetail;
import iclean.code.data.dto.response.booking.GetDetailBookingResponse;
import iclean.code.data.dto.response.bookingdetail.GetCheckOutResponseDetail;
import iclean.code.data.dto.response.helperinformation.GetPriorityResponse;
import iclean.code.data.enumjava.*;
import iclean.code.data.repository.*;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.booking.service.BookingService;
import iclean.code.function.serviceprice.service.ServicePriceService;
import iclean.code.function.common.service.FCMService;
import iclean.code.function.common.service.GoogleMapService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BookingServiceImpl implements BookingService {

    @Autowired
    private FCMService fcmService;
    @Value("${iclean.app.point.to.money}")
    private Double pointToMoney;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingDetailHelperRepository bookingDetailHelperRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${iclean.app.max.request.count}")
    private Integer maxRequestCount;
    @Autowired
    private BookingDetailStatusHistoryRepository bookingDetailStatusHistoryRepository;
    @Autowired
    private ServiceUnitRepository serviceUnitRepository;
    @Autowired
    private ServiceRegistrationRepository serviceRegistrationRepository;
    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private HelperInformationRepository helperInformationRepository;
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private SystemParameterRepository systemParameterRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private ServicePriceService servicePriceService;
    @Autowired
    GoogleMapService googleMapService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable, List<String> statuses, Boolean isHelper) {
        try {
            Page<Booking> bookings;
            List<BookingStatusEnum> bookingStatusEnums = null;
            if (!(statuses == null || statuses.isEmpty())) {
                bookingStatusEnums = statuses
                        .stream()
                        .map(element -> BookingStatusEnum.valueOf(element.toUpperCase()))
                        .collect(Collectors.toList());
            }
            String roleUser = userRepository.findByUserId(userId).getRole().getTitle().toUpperCase();
            if (Utils.isNullOrEmpty(roleUser))
                throw new UserNotHavePermissionException("User do not have permission to do this action");
            RoleEnum roleEnum = RoleEnum.valueOf(roleUser);
            switch (roleEnum) {
                case EMPLOYEE:
                    if (isHelper) {
                        bookings = !(statuses == null || statuses.isEmpty())
                                ? bookingRepository.findByHelperId(userId, bookingStatusEnums, BookingStatusEnum.ON_CART, pageable)
                                : bookingRepository.findByHelperId(userId, BookingStatusEnum.ON_CART, pageable);
                    } else {
                        bookings = !(statuses == null || statuses.isEmpty())
                                ? bookingRepository.findByRenterId(userId, bookingStatusEnums, BookingStatusEnum.ON_CART, pageable)
                                : bookingRepository.findByRenterId(userId, BookingStatusEnum.ON_CART, pageable);
                    }

                    break;
                case RENTER:
                    bookings = !(statuses == null || statuses.isEmpty())
                            ? bookingRepository.findByRenterId(userId, bookingStatusEnums, BookingStatusEnum.ON_CART, pageable)
                            : bookingRepository.findByRenterId(userId, BookingStatusEnum.ON_CART, pageable);

                    break;
                case MANAGER:
                case ADMIN:
                    bookings = !(statuses == null || statuses.isEmpty())
                            ? bookingRepository.findAllByBookingStatus(bookingStatusEnums, BookingStatusEnum.ON_CART, pageable)
                            : bookingRepository.findAllBooking(BookingStatusEnum.ON_CART, pageable);

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
    public ResponseEntity<ResponseObject> getBookingDetailByBookingId(Integer bookingId, Integer userId) {
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
    public ResponseEntity<ResponseObject> createBookingNow(CreateBookingRequestNow request, Integer renterId) {
        try {
            Booking booking = new Booking();
            Double priceDetail = servicePriceService
                    .getServicePrice(new GetServicePriceRequest(request.getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            Double priceHelper = servicePriceService
                    .getServiceHelperPrice(new GetServicePriceRequest(request.getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            BookingDetailStatusHistory bookingDetailStatusHistory = new BookingDetailStatusHistory();
            booking.setRenter(findAccount(renterId));
            booking.setTotalPrice(0.0);
            booking.setBookingStatus(BookingStatusEnum.NOT_YET);
            bookingDetailStatusHistory.setBookingDetailStatus(BookingDetailStatusEnum.NOT_YET);
            List<Address> addresses = addressRepository.findByUserIdAnAndIsDefault(renterId);
            Address addressDefault = null;
            if (!addresses.isEmpty()) {
                addressDefault = addresses.get(0);
            }
            if (addressDefault != null && ObjectUtils.anyNull(booking.getLongitude(),
                    booking.getLatitude(),
                    booking.getLocation(),
                    booking.getLocationDescription())) {
                booking.setLongitude(addressDefault.getLongitude());
                booking.setLatitude(addressDefault.getLatitude());
                booking.setLocation(addressDefault.getLocationName());
                booking.setLocationDescription(addressDefault.getDescription());
            }
            double price = booking.getTotalPrice();
            BookingDetail bookingDetail = new BookingDetail();
            ServiceUnit requestServiceUnit = findServiceUnitById(request.getServiceUnitId());
            bookingDetail.setServiceUnit(requestServiceUnit);
            bookingDetail.setNote(request.getNote());
            price = price + priceDetail;
            booking.setTotalPrice(price);
            booking.setTotalPriceActual(price);

            bookingDetail.setBooking(booking);
            bookingDetail.setPriceDetail(priceDetail);
            bookingDetail.setPriceHelper(priceHelper);
            bookingDetail.setWorkStart(request.getStartTime().toLocalTime());
            bookingDetail.setWorkDate(request.getStartTime().toLocalDate());
            bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.ON_CART);
            bookingDetail.setNote(request.getNote());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Booking Successfully!", booking));

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
    public ResponseEntity<ResponseObject> resendBooking(CheckOutCartRequest request, Integer id, Integer renterId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Booking booking = findBookingById(id);
            if (request != null && request.getAddressId() != null) {
                Address address = findAddressById(request.getAddressId());
                if (!Objects.equals(address.getUser().getUserId(), renterId)) {
                    throw new UserNotHavePermissionException("Address is not available!");
                }
                booking.setLongitude(address.getLongitude());
                booking.setLatitude(address.getLatitude());
                booking.setLocation(address.getLocationName());
                booking.setLocationDescription(address.getDescription());
            } else if (ObjectUtils.anyNull(booking.getLocation(),
                    booking.getLongitude(),
                    booking.getLatitude(),
                    booking.getLocationDescription())) {
                List<Address> addresses = addressRepository.findByUserIdAnAndIsDefault(renterId);
                Address addressDefault = null;
                if (!addresses.isEmpty()) {
                    addressDefault = addresses.get(0);
                }
                if (addressDefault != null) {
                    booking.setLongitude(addressDefault.getLongitude());
                    booking.setLatitude(addressDefault.getLatitude());
                    booking.setLocation(addressDefault.getLocationName());
                    booking.setLocationDescription(addressDefault.getDescription());
                } else {
                    throw new BadRequestException(MessageVariable.NEED_ADD_LOCATION_FOR_BOOKING);
                }
            }
            User renter = findAccount(renterId);
            if (!(booking.getRequestCount() >= getMaxRequestCount())) {
                throw new BadRequestException(MessageVariable.CANNOT_RESEND_THIS_BOOKING);
            }
            booking.setRequestCount(booking.getRequestCount() + 1);
            booking.setBookingStatus(BookingStatusEnum.NOT_YET);
            booking.setUpdateAt(Utils.getLocalDateTimeNow());
            booking.setBookingCode(Utils.generateRandomCode());
            if (request != null) {
                booking.setAutoAssign(request.getAutoAssign() != null ? request.getAutoAssign() : booking.getAutoAssign());
                booking.setUsingPoint(request.getUsingPoint() != null ? request.getUsingPoint() : booking.getUsingPoint());
                booking.setOption(objectMapper.writeValueAsString(request));
                if (request.getUsingPoint()) {
                    Wallet walletPoint = walletRepository.getWalletByUserIdAndType(renterId, WalletTypeEnum.POINT);
                    Double minusMoney = Utils.roundingNumber(walletPoint.getBalance() * getPointToMoney(), 1000D, RoundingMode.UP);
                    Double usingPoint;
                    if (minusMoney > booking.getTotalPrice()) {
                        booking.setTotalPriceActual(0D);
                        usingPoint = (minusMoney - booking.getTotalPrice()) / getPointToMoney();
                        usingPoint = Utils.roundingNumber(usingPoint, 1D, RoundingMode.DOWN);
                    } else {
                        booking.setTotalPriceActual(Utils.roundingNumber(booking.getTotalPrice() - minusMoney, 1000D, RoundingMode.DOWN));
                        usingPoint = walletPoint.getBalance();
                    }
                    createTransaction(new TransactionRequest(usingPoint,
                            MessageVariable.USING_POINT_FOR_BOOKING,
                            renter.getUserId(),
                            TransactionTypeEnum.WITHDRAW.name(),
                            WalletTypeEnum.POINT.name(),
                            booking.getBookingId())
                    );
                }
            }
            List<BookingDetailStatusHistory> bookingDetailStatusHistories = new ArrayList<>();
            List<BookingDetail> bookingDetails = booking.getBookingDetails();
            for (BookingDetail bookingDetail :
                    bookingDetails) {
                bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.NOT_YET);
                BookingDetailStatusHistory bookingDetailStatusHistory = new BookingDetailStatusHistory();
                bookingDetailStatusHistory.setBookingDetailStatus(BookingDetailStatusEnum.NOT_YET);
                bookingDetailStatusHistory.setBookingDetail(bookingDetail);
                bookingDetailStatusHistories.add(bookingDetailStatusHistory);
            }
            bookingDetailRepository.saveAll(bookingDetails);
            bookingDetailStatusHistoryRepository.saveAll(bookingDetailStatusHistories);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
            notificationRequestDto.setBody(String.format(MessageVariable.ORDER_SUCCESSFUL, booking.getBookingId()));
            Notification notification = new Notification();
            notification.setContent(notificationRequestDto.getBody());
            notification.setTitle(notification.getTitle());
            notification.setUser(renter);
            notificationRepository.save(notification);
            sendMessage(notificationRequestDto, findAccount(renterId));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Checkout Successfully!", null));

        } catch (Exception e) {
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createServiceToCart(AddBookingRequest request,
                                                              Integer userId) {
        try {
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            BookingDetailStatusHistory bookingDetailStatusHistory = null;
            Double priceDetail = servicePriceService
                    .getServicePrice(new GetServicePriceRequest(request.getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            Double priceHelper = servicePriceService
                    .getServiceHelperPrice(new GetServicePriceRequest(request.getServiceUnitId(),
                            request.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            if (booking == null) {
                bookingDetailStatusHistory = new BookingDetailStatusHistory();
                booking = new Booking();
                booking.setRenter(findAccount(userId));
                booking.setTotalPrice(0.0);
                booking.setBookingStatus(BookingStatusEnum.ON_CART);
                bookingDetailStatusHistory.setBookingDetailStatus(BookingDetailStatusEnum.ON_CART);
            }
            List<Address> addresses = addressRepository.findByUserIdAnAndIsDefault(userId);
            Address addressDefault = null;
            if (!addresses.isEmpty()) {
                addressDefault = addresses.get(0);
            }
            if (addressDefault != null && ObjectUtils.anyNull(booking.getLongitude(),
                    booking.getLatitude(),
                    booking.getLocation(),
                    booking.getLocationDescription())) {
                booking.setLongitude(addressDefault.getLongitude());
                booking.setLatitude(addressDefault.getLatitude());
                booking.setLocation(addressDefault.getLocationName());
                booking.setLocationDescription(addressDefault.getDescription());
            }
            double price = booking.getTotalPrice();
            BookingDetail bookingDetail = new BookingDetail();
            ServiceUnit requestServiceUnit = findServiceUnitById(request.getServiceUnitId());
            Optional<BookingDetail> checkCurrentDetail = bookingDetailRepository
                    .findByServiceIdAndBookingStatus(requestServiceUnit.getService().getServiceId(), BookingStatusEnum.ON_CART);
            if (checkCurrentDetail.isPresent()) {
                bookingDetail = checkCurrentDetail.get();
                bookingDetail.setServiceUnit(requestServiceUnit);
                price = price - bookingDetail.getPriceDetail() + priceDetail;
            } else {
                bookingDetail.setServiceUnit(requestServiceUnit);
                bookingDetail.setNote(request.getNote());
                price = price + priceDetail;
            }
            booking.setTotalPrice(price);
            booking.setTotalPriceActual(price);
            bookingRepository.save(booking);

            bookingDetail.setBooking(booking);
            bookingDetail.setPriceDetail(priceDetail);
            bookingDetail.setPriceHelper(priceHelper);
            bookingDetail.setWorkStart(request.getStartTime().toLocalTime());
            bookingDetail.setWorkDate(request.getStartTime().toLocalDate());
            bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.ON_CART);
            bookingDetail.setNote(request.getNote());

            bookingDetailRepository.save(bookingDetail);
            if (bookingDetailStatusHistory != null) {
                bookingDetailStatusHistory.setBookingDetail(bookingDetail);
                bookingDetailStatusHistoryRepository.save(bookingDetailStatusHistory);
            }

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
                        booking.setTotalPriceActual(Utils.roundingNumber(booking.getTotalPrice() - minusMoney, 1000D, RoundingMode.DOWN));
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
            List<BookingDetailStatusHistory> bookingDetailStatusHistories =
                    bookingDetailStatusHistoryRepository.findByBookingId(booking.getBookingId());
            bookingDetailStatusHistoryRepository.deleteAll(bookingDetailStatusHistories);
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
                break;
            default:
                throw new UserNotHavePermissionException("User do not have permission to do this action");
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteServiceOnCart(Integer userId, Integer cartItemId) {
        try {
            Optional<BookingDetail> bookingDetail = bookingDetailRepository
                    .findByBookingDetailIdAndBookingStatus(cartItemId, BookingDetailStatusEnum.ON_CART);
            if (bookingDetail.isPresent()) {
                Booking booking = bookingDetail.get().getBooking();
                Double totalPrice = booking.getTotalPrice() - bookingDetail.get().getPriceDetail();
                booking.setTotalPriceActual(totalPrice);
                booking.setTotalPrice(totalPrice);
                isPermission(userId, bookingDetail.get());
                bookingRepository.save(booking);
                bookingDetailStatusHistoryRepository.deleteAll(bookingDetail.get().getBookingDetailStatusHistories());
                bookingDetailRepository.delete(bookingDetail.get());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Delete a service on cart successful", null));
            }
            throw new NotFoundException(String.format("The service with detail ID: %s is not on this cart!", cartItemId));

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
    @Transactional
    public ResponseEntity<ResponseObject> checkoutCart(Integer userId, CheckOutCartRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            if (request != null && request.getAddressId() != null) {
                Address address = findAddressById(request.getAddressId());
                if (!Objects.equals(address.getUser().getUserId(), userId)) {
                    throw new UserNotHavePermissionException("Address is not available!");
                }
                booking.setLongitude(address.getLongitude());
                booking.setLatitude(address.getLatitude());
                booking.setLocation(address.getLocationName());
                booking.setLocationDescription(address.getDescription());
            } else if (ObjectUtils.anyNull(booking.getLocation(),
                    booking.getLongitude(),
                    booking.getLatitude(),
                    booking.getLocationDescription())) {
                List<Address> addresses = addressRepository.findByUserIdAnAndIsDefault(userId);
                Address addressDefault = null;
                if (!addresses.isEmpty()) {
                    addressDefault = addresses.get(0);
                }
                if (addressDefault != null) {
                    booking.setLongitude(addressDefault.getLongitude());
                    booking.setLatitude(addressDefault.getLatitude());
                    booking.setLocation(addressDefault.getLocationName());
                    booking.setLocationDescription(addressDefault.getDescription());
                } else {
                    throw new BadRequestException(MessageVariable.NEED_ADD_LOCATION_FOR_BOOKING);
                }
            }
            User renter = findAccount(userId);
            booking.setRequestCount(1);
            booking.setBookingStatus(BookingStatusEnum.NOT_YET);
            booking.setUpdateAt(Utils.getLocalDateTimeNow());
            booking.setBookingCode(Utils.generateRandomCode());
            if (request != null) {
                booking.setAutoAssign(request.getAutoAssign() != null ? request.getAutoAssign() : booking.getAutoAssign());
                booking.setUsingPoint(request.getUsingPoint() != null ? request.getUsingPoint() : booking.getUsingPoint());
                booking.setOption(objectMapper.writeValueAsString(request));
                if (request.getUsingPoint()) {
                    Wallet walletPoint = walletRepository.getWalletByUserIdAndType(userId, WalletTypeEnum.POINT);
                    Double minusMoney = Utils.roundingNumber(walletPoint.getBalance() * getPointToMoney(), 1000D, RoundingMode.UP);
                    Double usingPoint;
                    if (minusMoney > booking.getTotalPrice()) {
                        booking.setTotalPriceActual(0D);
                        usingPoint = (minusMoney - booking.getTotalPrice()) / getPointToMoney();
                        usingPoint = Utils.roundingNumber(usingPoint, 1D, RoundingMode.DOWN);
                    } else {
                        booking.setTotalPriceActual(Utils.roundingNumber(booking.getTotalPrice() - minusMoney, 1000D, RoundingMode.DOWN));
                        usingPoint = walletPoint.getBalance();
                    }
                    createTransaction(new TransactionRequest(usingPoint,
                            MessageVariable.USING_POINT_FOR_BOOKING,
                            renter.getUserId(),
                            TransactionTypeEnum.WITHDRAW.name(),
                            WalletTypeEnum.POINT.name(),
                            booking.getBookingId())
                    );
                }
            }
            boolean checkTransactionMoney = createTransaction(new TransactionRequest(booking.getTotalPriceActual(),
                    MessageVariable.USING_MONEY_FOR_BOOKING,
                    userId,
                    TransactionTypeEnum.WITHDRAW.name(),
                    WalletTypeEnum.MONEY.name(),
                    booking.getBookingId()));
            NotificationRequestDto notificationRequestDtoPayment = new NotificationRequestDto();
            notificationRequestDtoPayment.setTitle(String.format(MessageVariable.PAYMENT_A_SERVICE, booking.getBookingCode()));
            if (checkTransactionMoney) {
                notificationRequestDtoPayment.setBody(MessageVariable.PAYMENT_SUCCESS);
            } else {
                throw new BadRequestException(MessageVariable.PAYMENT_FAIL);
            }
            sendMessage(notificationRequestDtoPayment, renter);
            Notification notificationPayment = new Notification();
            notificationPayment.setUser(renter);
            notificationPayment.setTitle(notificationRequestDtoPayment.getTitle());
            notificationPayment.setContent(notificationRequestDtoPayment.getBody());
            notificationRepository.save(notificationPayment);
            List<BookingDetailStatusHistory> bookingDetailStatusHistories = new ArrayList<>();
            List<BookingDetail> bookingDetails = booking.getBookingDetails();
            for (BookingDetail bookingDetail :
                    bookingDetails) {
                bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.NOT_YET);
                BookingDetailStatusHistory bookingDetailStatusHistory = new BookingDetailStatusHistory();
                bookingDetailStatusHistory.setBookingDetailStatus(BookingDetailStatusEnum.NOT_YET);
                bookingDetailStatusHistory.setBookingDetail(bookingDetail);
                bookingDetailStatusHistories.add(bookingDetailStatusHistory);
            }
            bookingDetailRepository.saveAll(bookingDetails);
            bookingDetailStatusHistoryRepository.saveAll(bookingDetailStatusHistories);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
            notificationRequestDto.setBody(String.format(MessageVariable.ORDER_SUCCESSFUL, booking.getBookingId()));
            Notification notification = new Notification();
            notification.setContent(notificationRequestDto.getBody());
            notification.setTitle(notification.getTitle());
            notification.setUser(renter);
            notificationRepository.save(notification);
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

    private List<String> convertToListFcmToken(List<DeviceToken> deviceTokens) {
        return deviceTokens.stream()
                .map(DeviceToken::getFcmToken)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<ResponseObject> acceptOrRejectBooking(Integer bookingId, AcceptRejectBookingRequest request, Integer managerId) {
        try {
            Booking booking = findBookingById(bookingId);
            User renter = booking.getRenter();
            booking.setManager(userRepository.findByUserId(managerId));
            BookingStatusEnum bookingStatusEnum = BookingStatusEnum.valueOf(request.getAction().toUpperCase());
            BookingDetailStatusEnum bookingDetailStatusEnum = BookingDetailStatusEnum.valueOf(request.getAction().toUpperCase());
            switch (bookingStatusEnum) {
                case REJECTED:
                    if (Utils.isNullOrEmpty(request.getRejectionReasonDetail())) {
                        throw new BadRequestException("Rejection Reason are required");
                    }
                    booking.setRejectionReason(findById(request.getRejectionReasonId()));
                    booking.setRjReasonDescription(request.getRejectionReasonDetail().trim());
                    if (booking.getUsingPoint()) {
                        Transaction transactionPoint = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(bookingId,
                                WalletTypeEnum.POINT, TransactionTypeEnum.WITHDRAW, renter.getUserId());
                        if (transactionPoint != null) {
                            createTransaction(new TransactionRequest(
                                    transactionPoint.getAmount(),
                                    String.format(MessageVariable.REFUND_POINT_CANCEL_BOOKING, bookingId),
                                    renter.getUserId(),
                                    TransactionTypeEnum.DEPOSIT.name(),
                                    WalletTypeEnum.POINT.name()
                            ));
                        }
                    }
                    createTransaction(new TransactionRequest(booking.getTotalPriceActual(),
                            MessageVariable.REFUND_REJECT_BOOKING,
                            renter.getUserId(),
                            TransactionTypeEnum.DEPOSIT.name(),
                            WalletTypeEnum.MONEY.name(),
                            bookingId));
                    NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                    notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                    notificationRequestDto.setBody(MessageVariable.REFUND_REJECT_BOOKING);
                    sendMessage(notificationRequestDto, renter);
                    break;
                case APPROVED:
                    break;
                default:
                    throw new UserNotHavePermissionException("Cannot update to this status");
            }
            booking.setUpdateAt(Utils.getLocalDateTimeNow());
            List<BookingDetailStatusHistory> bookingDetailStatusHistories = new ArrayList<>();
            LocalDateTime now = Utils.getLocalDateTimeNow();
            DayOfWeek currentDate = now.getDayOfWeek();
            List<BookingDetail> bookingDetails = booking.getBookingDetails();
            List<BookingDetailHelper> needToAssigns = new ArrayList<>();
            int numberReject = 0;
            for (BookingDetail bookingDetail :
                    bookingDetails) {
                BookingDetailStatusHistory bookingDetailStatusHistory = new BookingDetailStatusHistory();
                bookingDetailStatusHistory.setBookingDetailStatus(bookingDetailStatusEnum);
                bookingDetailStatusHistory.setBookingDetail(bookingDetail);
                bookingDetailStatusHistories.add(bookingDetailStatusHistory);

                if (booking.getAutoAssign()) {
                    LocalDateTime startDateTime = LocalDateTime.of(bookingDetail.getWorkDate(), bookingDetail.getWorkStart());
                    LocalDateTime endDateTime = Utils.plusLocalDateTime(startDateTime, bookingDetail.getServiceUnit().getUnit().getUnitValue());
                    List<HelperInformation> helpersInformation = helperInformationRepository.findAllByWorkScheduleStartEndAndServiceId(startDateTime, endDateTime, currentDate,
                            bookingDetail.getServiceUnit().getService().getServiceId(), ServiceHelperStatusEnum.ACTIVE, BookingDetailHelperStatusEnum.ACTIVE);
                    HelperInformation helperInformation = getPriority(helpersInformation, bookingDetail.getServiceUnit().getService().getServiceId());
                    BookingDetailHelper bookingDetailHelper = new BookingDetailHelper();
                    bookingDetailHelper.setBookingDetail(bookingDetail);
                    bookingDetailHelper.setBookingDetailHelperStatus(BookingDetailHelperStatusEnum.ACTIVE);
                    if (helperInformation != null) {
                        bookingDetail.setBookingDetailStatus(bookingDetailStatusEnum);
                        ServiceRegistration serviceRegistration = serviceRegistrationRepository.findByServiceIdAndUserId(bookingDetail.getServiceUnit().getService().getServiceId(),
                                helperInformation.getUser().getUserId());
                        bookingDetailHelper.setServiceRegistration(serviceRegistration);
                        needToAssigns.add(bookingDetailHelper);
                    } else {
                        numberReject++;
                        bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.REJECTED);
                        if (booking.getUsingPoint()) {
                            Transaction transactionPoint = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(bookingId,
                                    WalletTypeEnum.POINT, TransactionTypeEnum.WITHDRAW, renter.getUserId());
                            if (transactionPoint != null) {
                                createTransaction(new TransactionRequest(
                                        transactionPoint.getAmount(),
                                        String.format(MessageVariable.REFUND_POINT_CANCEL_BOOKING, bookingId),
                                        renter.getUserId(),
                                        TransactionTypeEnum.DEPOSIT.name(),
                                        WalletTypeEnum.POINT.name()
                                ));
                            }
                        }
                        Transaction transaction = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(bookingId,
                                WalletTypeEnum.MONEY, TransactionTypeEnum.WITHDRAW, renter.getUserId());
                        createTransaction(new TransactionRequest(transaction.getAmount(),
                                MessageVariable.REFUND_REJECT_BOOKING,
                                renter.getUserId(),
                                TransactionTypeEnum.DEPOSIT.name(),
                                WalletTypeEnum.MONEY.name(),
                                bookingId));
                        NotificationRequestDto notificationRequestDto1 = new NotificationRequestDto();
                        notificationRequestDto1.setTitle(MessageVariable.TITLE_APP);
                        notificationRequestDto1.setBody(String.format(MessageVariable.NOT_FOUND_HELPER_FOR_THIS_SERVICE,
                                bookingDetail.getServiceUnit().getService().getServiceName()));
                        sendMessage(notificationRequestDto1, renter);
                        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                        notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                        notificationRequestDto.setBody(MessageVariable.REFUND_REJECT_BOOKING);
                        sendMessage(notificationRequestDto, renter);
                        break;
                    }
                }
            }

            booking.setBookingStatus(bookingStatusEnum);
            if (numberReject == booking.getBookingDetails().size()) {
                booking.setBookingStatus(BookingStatusEnum.REJECTED);
            }
            bookingDetailHelperRepository.saveAll(needToAssigns);
            bookingDetailRepository.saveAll(bookingDetails);
            bookingDetailStatusHistoryRepository.saveAll(bookingDetailStatusHistories);
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

    @Override
    public ResponseEntity<ResponseObject> cancelBooking(Integer bookingId, Integer renterId) {
        try {
            Booking booking = findBookingById(bookingId);
            List<BookingDetail> bookingDetails = booking.getBookingDetails();
            isPermission(renterId, booking);

            switch (booking.getBookingStatus()) {
                case NOT_YET:
                case NO_MONEY:
                    Transaction transaction = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(
                            bookingId,
                            WalletTypeEnum.POINT,
                            TransactionTypeEnum.WITHDRAW,
                            renterId);
                    if (transaction != null) {
                        createTransaction(new TransactionRequest(
                                transaction.getAmount(),
                                String.format(MessageVariable.REFUND_POINT_CANCEL_BOOKING, booking.getBookingCode()),
                                renterId,
                                TransactionTypeEnum.DEPOSIT.name(),
                                WalletTypeEnum.POINT.name(),
                                bookingId));
                    }

                    Transaction transactionMoney = transactionRepository.findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(
                            bookingId,
                            WalletTypeEnum.MONEY,
                            TransactionTypeEnum.WITHDRAW,
                            renterId);
                    if (transactionMoney != null) {
                        createTransaction(new TransactionRequest(
                                transactionMoney.getAmount(),
                                String.format(MessageVariable.REFUND_REJECT_BOOKING, booking.getBookingCode()),
                                renterId,
                                TransactionTypeEnum.DEPOSIT.name(),
                                WalletTypeEnum.MONEY.name(),
                                bookingId));
                    }
                    booking.setBookingStatus(BookingStatusEnum.CANCELED);
                    booking.setUpdateAt(Utils.getLocalDateTimeNow());
                    List<BookingDetailStatusHistory> bookingDetailStatusHistories = new ArrayList<>();
                    for (BookingDetail bookingDetail :
                            bookingDetails) {
                        bookingDetail.setBookingDetailStatus(BookingDetailStatusEnum.CANCEL_BY_RENTER);
                        BookingDetailStatusHistory bookingDetailStatusHistory = new BookingDetailStatusHistory();
                        bookingDetailStatusHistory.setBookingDetail(bookingDetail);
                        bookingDetailStatusHistory.setBookingDetailStatus(BookingDetailStatusEnum.CANCEL_BY_RENTER);
                    }
                    bookingDetailStatusHistoryRepository.saveAll(bookingDetailStatusHistories);
                    bookingDetailRepository.saveAll(bookingDetails);
                    bookingRepository.save(booking);
                    break;
                case CANCELED:
                    throw new BadRequestException(String.format(MessageVariable.ALREADY_CANCEL_BOOKING, booking.getBookingCode()));
                default:
                    throw new BadRequestException(String.format(MessageVariable.CANNOT_CANCEL_BOOKING, booking.getBookingCode()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Make a payment booking successful", null));
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

    @Override
    public ResponseEntity<ResponseObject> getCheckoutCart(Integer renterId) {
        try {
            Booking booking = bookingRepository.findCartByRenterId(renterId, BookingStatusEnum.ON_CART);

            if (booking != null && !booking.getBookingDetails().isEmpty()) {
                CheckOutCartRequest checkOutCartRequest = new CheckOutCartRequest(null, booking.getUsingPoint(), booking.getAutoAssign());
                if (booking.getUsingPoint()) {
                    Wallet walletPoint = walletRepository.getWalletByUserIdAndType(renterId, WalletTypeEnum.POINT);
                    Double minusMoney = Utils.roundingNumber(walletPoint.getBalance() * getPointToMoney(), 1000D, RoundingMode.UP);
                    if (minusMoney > booking.getTotalPrice()) {
                        booking.setTotalPriceActual(0D);
                    } else {
                        booking.setTotalPriceActual(Utils.roundingNumber(booking.getTotalPrice() - minusMoney, 1000D, RoundingMode.DOWN));
                    }
                }
                GetCheckOutResponseDetail responseDetail = modelMapper.map(booking, GetCheckOutResponseDetail.class);
                responseDetail.setAutoAssign(Optional.of(checkOutCartRequest)
                        .map(CheckOutCartRequest::getAutoAssign)
                        .orElse(false));
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Checkout Response Successfully!", responseDetail));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Checkout Response Successfully!", null));

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
    public ResponseEntity<ResponseObject> updateCheckoutCart(Integer renterId, CheckOutCartRequest request) {
        try {
            if (Objects.isNull(request)) {
                request = new CheckOutCartRequest();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Booking booking = bookingRepository.findCartByRenterId(renterId, BookingStatusEnum.ON_CART);
            if (booking == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                            "User do not have any cart", null));
            if (Objects.nonNull(request.getAddressId())) {
                Address address = findAddressById(request.getAddressId());
                if (!Objects.equals(address.getUser().getUserId(), renterId)) {
                    throw new UserNotHavePermissionException("Address is not available!");
                }
                booking.setLongitude(address.getLongitude());
                booking.setLatitude(address.getLatitude());
                booking.setLocation(address.getLocationName());
                booking.setLocationDescription(address.getDescription());
            }
            booking.setUsingPoint(request.getUsingPoint());
            booking.setAutoAssign(request.getAutoAssign());
            booking.setOption(objectMapper.writeValueAsString(request));
            bookingRepository.save(booking);
            if (booking.getUsingPoint()) {
                Wallet walletPoint = walletRepository.getWalletByUserIdAndType(renterId, WalletTypeEnum.POINT);
                Double minusMoney = Utils.roundingNumber(walletPoint.getBalance() * getPointToMoney(), 1000D, RoundingMode.UP);
                if (minusMoney > booking.getTotalPrice()) {
                    booking.setTotalPriceActual(0D);
                } else {
                    booking.setTotalPriceActual(Utils.roundingNumber(booking.getTotalPrice() - minusMoney, 1000D, RoundingMode.DOWN));
                }
            }
            CheckOutCartRequest checkOutCartRequest = objectMapper.readValue(booking.getOption(), CheckOutCartRequest.class);
            GetCheckOutResponseDetail responseDetail = modelMapper.map(booking, GetCheckOutResponseDetail.class);
            responseDetail.setUsingPoint(request.getUsingPoint());
            responseDetail.setAutoAssign(Optional.ofNullable(checkOutCartRequest)
                    .map(CheckOutCartRequest::getAutoAssign)
                    .orElse(false));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update checkout Successfully!", responseDetail));

        } catch (Exception e) {
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
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
                                        .collect(Collectors.joining(", ")));
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
        return Utils.convertToPageResponse(bookings, dtoList);
    }

    private User findAccount(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    private RejectionReason findById(int id) {
        return rejectionReasonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rejection Reason is not exist"));
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

    private Integer getMaxRequestCount() {
        SystemParameter systemParameter = systemParameterRepository.findSystemParameterByParameterField(SystemParameterField.REQUEST_BOOKING_COUNT);
        try {
            return Integer.parseInt(systemParameter.getParameterValue());
        } catch (Exception e) {
            return maxRequestCount;
        }
    }
}
