package iclean.code.function.booking.service.impl;

import iclean.code.config.MessageVariable;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.booking.*;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetBookingHistoryResponse;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.booking.GetCartResponseDetail;
import iclean.code.data.dto.response.bookingdetail.GetBookingDetailResponse;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import iclean.code.data.enumjava.BookingStatusEnum;
import iclean.code.data.enumjava.RoleEnum;
import iclean.code.data.repository.*;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.booking.service.BookingService;
import iclean.code.function.serviceprice.service.ServicePriceService;
import iclean.code.service.FCMService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static iclean.code.data.enumjava.RoleEnum.EMPLOYEE;

@Service
@Log4j2
public class BookingServiceImpl implements BookingService {

    @Autowired
    private FCMService fcmService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

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
    private ServicePriceService servicePriceService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable, boolean isAll) {
        try {
            Page<Booking> bookings = Page.empty();
            String roleUser = userRepository.findByUserId(userId).getRole().getTitle().toUpperCase();
            if (Utils.isNullOrEmpty(roleUser))
                throw new UserNotHavePermissionException("User do not have permission to do this action");
            RoleEnum roleEnum = RoleEnum.valueOf(roleUser);
            switch (roleEnum) {
                case EMPLOYEE:
                    bookingRepository.findByHelperId(userId, pageable);
                    break;
                case RENTER:
                    bookings = bookingRepository.findByRenterId(userId, pageable);
                    break;
                case MANAGER:
                    if (isAll) {
                        bookings = bookingRepository.findAllBooking(pageable);
                    } else {
                        bookings = bookingRepository.findByManagerId(userId, pageable);
                    }
                    break;
                case ADMIN:
                    bookings = bookingRepository.findAllBooking(pageable);
                    break;
                default:
                    throw new UserNotHavePermissionException("User do not have permission to do this action");
            }
            List<GetBookingResponse> dtoList = bookings
                    .stream()
                    .map(booking -> {
                                GetBookingResponse response = modelMapper.map(booking, GetBookingResponse.class);
                                response.setServiceName(booking.getBookingDetails()
                                        .stream()
                                        .map(detail -> detail.getServiceUnit()
                                                .getService().getServiceName())
                                        .collect(Collectors.joining(",")));
                                if (booking.getBookingStatusHistories().size() > 0) {
                                    response.setBookingStatus(booking.getBookingStatusHistories()
                                            .get(booking.getBookingStatusHistories().size() - 1).getBookingStatus().getValue());
                                }
                                return response;
                            }
                    )
                    .collect(Collectors.toList());

            PageResponseObject pageResponseObject = Utils.convertToPageResponse(bookings, Collections.singletonList(dtoList));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "All Booking", pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
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
                case MANAGER:
                case ADMIN:
                    break;
                default:
                    throw new UserNotHavePermissionException("User do not have permission to do this action");
            }

            GetDetailBookingResponse response = modelMapper.map(booking, GetDetailBookingResponse.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Booking", response));
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

//    private void isPermissionForHelper(Integer userId, Booking booking) throws UserNotHavePermissionException {
//            if (!Objects.equals(booking.getRenter().getUserId(), userId))
//                throw new UserNotHavePermissionException("User do not have permission to do this action");
//    }

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
            if (booking == null) {
                bookingStatusHistory = new BookingStatusHistory();
                booking = new Booking();
                booking.setRenter(findAccount(userId));
                booking.setTotalPrice(0.0);
                bookingStatusHistory.setBookingStatus(BookingStatusEnum.ON_CART);
            }
            double price;
            BookingDetail bookingDetail = new BookingDetail();
            Optional<BookingDetail> checkCurrentDetail = bookingDetailRepository
                    .findByServiceUnitIdAndBookingStatus(request.getServiceUnitId(), BookingStatusEnum.ON_CART);
            if (checkCurrentDetail.isPresent()) {
                bookingDetail = checkCurrentDetail.get();
                price = booking.getTotalPrice() - bookingDetail.getPriceDetail() + priceDetail;
            } else {
                ServiceUnit serviceUnit = findServiceUnitById(request.getServiceUnitId());
                bookingDetail.setServiceUnit(serviceUnit);
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
    public ResponseEntity<ResponseObject> getCart(Integer userId) {
        try {
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            if (booking != null && !booking.getBookingDetails().isEmpty()) {
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

    private boolean isPermission(Integer userId, BookingDetail booking) throws UserNotHavePermissionException {
        if (!Objects.equals(booking.getBooking().getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
        return true;
    }

    private boolean isPermission(Integer userId, Booking booking) throws UserNotHavePermissionException {
        if (!Objects.equals(booking.getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
        return true;
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
            Booking booking = bookingRepository.findCartByRenterId(userId, BookingStatusEnum.ON_CART);
            Address address = findAddressById(request.getAddressId());
            if (!Objects.equals(address.getUser().getUserId(), userId)) {
                throw new UserNotHavePermissionException("Address is not available!");
            }
            booking.setLongitude(address.getLongitude());
            booking.setLatitude(address.getLatitude());
            booking.setLocation(address.getLocationName());
            booking.setLocationDescription(address.getDescription());
            booking.setRequestCount(1);
            booking.setUpdateAt(Utils.getDateTimeNow());

            // --- CheckoutCartRequest ---

            // ---
            BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
            bookingStatusHistory.setBookingStatus(BookingStatusEnum.NOT_YET);
            bookingStatusHistory.setBooking(booking);
            bookingStatusHistoryRepository.save(bookingStatusHistory);

            //SEND NOTIFICATION
            List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserId(userId);
            if (!deviceTokens.isEmpty()) {
                NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
                notificationRequestDto.setTarget(convertToListFcmToken(deviceTokens));
                notificationRequestDto.setTitle(MessageVariable.TITLE_APP);
                notificationRequestDto.setBody(String.format(MessageVariable.ORDER_SUCCESSFUL, booking.getBookingId()));

                fcmService.sendPnsToTopic(notificationRequestDto);
            }

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
    public ResponseEntity<ResponseObject> updateStatusBooking(Integer bookingId,
                                                              Integer userId,
                                                              UpdateStatusBookingRequest request) {
        try {

            Booking bookingForUpdateStatus = new Booking();
            Booking booking = findBookingById(bookingId);

            BookingStatusHistory bookingStatusHistory = bookingStatusHistoryRepository.findTheLatestBookingStatusByBookingId(bookingId);
            if (bookingStatusHistory != null) {
                if (Objects.equals(bookingStatusHistory.getBookingStatus(), BookingStatusEnum.FINISHED)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                                    , "You can't update FINISHED Booking", null));
                }
            }
            BookingStatusEnum optionalBookingStatus = BookingStatusEnum.valueOf(request.getBookingStatus().toUpperCase());
            User userUpdate = findAccount(userId);

            if (Objects.equals(RoleEnum.MANAGER.name(), userUpdate.getRole().getTitle().toUpperCase())) {
                bookingForUpdateStatus = mappingUpdateBookingForManager(booking, optionalBookingStatus, request);
            } else if (Objects.equals(EMPLOYEE.name(), userUpdate.getRole().getTitle().toUpperCase())) {
//                if (booking.getRenter() != null) {
//                    if (!Objects.equals(booking.getEmployee().getUserId(), userId))
//                        throw new UserNotHavePermissionException();
//                }
                bookingForUpdateStatus = mappingUpdateBookingForEmployee(booking, optionalBookingStatus, userId, request);
            }
            bookingRepository.save(bookingForUpdateStatus);

            //SEND NOTIFICATION
            List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserId(userId);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setTarget(convertToListFcmToken(deviceTokens));
            notificationRequestDto.setTitle("iClean - Helping Hand Hub Platform");
            notificationRequestDto.setBody("Đơn hàng " + booking.getBookingId() + " của bạn đã được cập nhật trạng thái mới.");

            fcmService.sendPnsToTopic(notificationRequestDto);
            //---------

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Status Booking Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString()
                                , e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateStatusBookingAsRenter(Integer bookingId,
                                                                      Integer userId,
                                                                      UpdateStatusBookingAsRenterRequest bookingRequest) {
        try {
            Booking bookingForUpdateStatus = new Booking();
            Booking booking = findBookingById(bookingId);

            if (!Objects.equals(booking.getRenter().getUserId(), userId))
                throw new UserNotHavePermissionException();

            BookingStatusEnum optionalBookingStatus = BookingStatusEnum.valueOf(bookingRequest.getBookingStatus().toUpperCase());

            BookingStatusHistory bookingStatusHistory = bookingStatusHistoryRepository.findTheLatestBookingStatusByBookingId(bookingId);
            if (bookingStatusHistory != null) {
                if (Objects.equals(bookingStatusHistory.getBookingStatus(), BookingStatusEnum.FINISHED)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                                    , "You can't update FINISHED Booking", null));
                }
            }
            User userUpdate = findAccount(userId);
            if (Objects.equals(RoleEnum.RENTER.name(), userUpdate.getRole().getTitle().toUpperCase())) {
                bookingForUpdateStatus = mappingUpdateBookingForRenter(booking, optionalBookingStatus, bookingRequest.getEmpId(), bookingRequest);
            }
            bookingRepository.save(bookingForUpdateStatus);

            //SEND NOTIFICATION
            List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserId(userId);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setTarget(convertToListFcmToken(deviceTokens));
            notificationRequestDto.setTitle("iClean - Helping Hand Hub Platform");
            notificationRequestDto.setBody("Đơn hàng " + booking.getBookingId() + " của bạn đã được cập nhật trạng thái mới.");

            fcmService.sendPnsToTopic(notificationRequestDto);
            //---------
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Status Booking Successfully!", null));
        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString()
                                , e.getMessage(), null));
            }
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getBookingHistory(int userId, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findBookingHistoryByUserId(userId, pageable);

        List<GetBookingHistoryResponse> dtoList = bookings
                .stream()
                .map(booking -> modelMapper.map(booking, GetBookingHistoryResponse.class))
                .collect(Collectors.toList());
        PageResponseObject pageResponseObject = Utils.convertToPageResponse(bookings, Collections.singletonList(dtoList));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Booking", pageResponseObject));
    }

    private Booking mappingUpdateBookingForManager(Booking optionalBooking,
                                                   BookingStatusEnum optionalBookingStatus,
                                                   UpdateStatusBookingRequest request) throws UserNotHavePermissionException {

        optionalBooking.setRequestCount(optionalBooking.getRequestCount() + 1);
//        optionalBooking.setBookingStatusHistories(optionalBookingStatus);
        optionalBooking.setUpdateAt(Utils.getDateTimeNow());

        Booking booking = modelMapper.map(optionalBooking, Booking.class);

        if (Objects.equals(BookingStatusEnum.REJECTED, optionalBookingStatus)) {
            if (request.getRejectReasonId() != null) {
                RejectionReason rejectionReason = findReject(request.getRejectReasonId());
                booking.setRejectionReason(rejectionReason);
            }
        } else if (BookingStatusEnum.APPROVED == optionalBookingStatus) {
            booking.setAcceptDate(LocalDateTime.now());
        } else {
            throw new UserNotHavePermissionException("You can't update this status");
        }
        addBookingStatusHistory(optionalBooking, optionalBookingStatus);
        return booking;
    }

    private Booking mappingUpdateBookingForEmployee(Booking optionalBooking,
                                                    BookingStatusEnum optionalBookingStatus,
                                                    Integer empId,
                                                    UpdateStatusBookingRequest request) throws UserNotHavePermissionException {

        optionalBooking.setRequestCount(optionalBooking.getRequestCount() + 1);
//        optionalBooking.setBookingStatusHistories(optionalBookingStatus);
        optionalBooking.setUpdateAt(Utils.getDateTimeNow());

        Booking booking = modelMapper.map(optionalBooking, Booking.class);

        if (Objects.equals(BookingStatusEnum.EMPLOYEE_ACCEPTED, optionalBookingStatus)) {
            addBookingEmployee(optionalBooking, empId);

        } else if (BookingStatusEnum.EMPLOYEE_CANCELED == optionalBookingStatus) {
            if (request.getRejectReasonId() != null) {
                RejectionReason rejectionReason = findReject(request.getRejectReasonId());
                booking.setRejectionReason(rejectionReason);
            }
        } else if (BookingStatusEnum.IN_PROCESSING == optionalBookingStatus) {
//            if (booking.getEmployee() == null) {
//                throw new NotFoundException("Booking này hiện tại chưa có nhân viên");
//            }
        } else if (BookingStatusEnum.FINISHED == optionalBookingStatus) {
//            if (booking.getEmployee() == null) {
//                throw new NotFoundException("Booking này hiện tại chưa có nhân viên");
//            }
        } else {
            throw new UserNotHavePermissionException("You can't update this status");
        }
        addBookingStatusHistory(optionalBooking, optionalBookingStatus);
        return booking;
    }

    private Booking mappingUpdateBookingForRenter(Booking optionalBooking,
                                                  BookingStatusEnum optionalBookingStatus,
                                                  Integer empId,
                                                  UpdateStatusBookingAsRenterRequest request) throws UserNotHavePermissionException {

        optionalBooking.setRequestCount(optionalBooking.getRequestCount() + 1);
        optionalBooking.setUpdateAt(Utils.getDateTimeNow());

        Booking booking = modelMapper.map(optionalBooking, Booking.class);

        if (Objects.equals(BookingStatusEnum.RENTER_ASSIGNED, optionalBookingStatus)) {
//            addBookingEmployee(optionalBooking, empId);
            BookingDetailHelper bookingDetailHelper = findEmployeeBooking(empId);
            bookingDetailHelper.setBookingDetailHelperStatus(BookingDetailHelperStatusEnum.ACTIVE);
            bookingEmployeeRepository.save(bookingDetailHelper);

            User employee = findAccount(empId, EMPLOYEE.name());
//            booking.setEmployee(employee);

        } else if (BookingStatusEnum.RENTER_CANCELED == optionalBookingStatus) {
            if (request.getRejectReasonId() != null) {
                RejectionReason rejectionReason = findReject(request.getRejectReasonId());
                booking.setRejectionReason(rejectionReason);
            }
        } else {
            throw new UserNotHavePermissionException("You can't update this status");
        }
        addBookingStatusHistory(optionalBooking, optionalBookingStatus);
        return booking;
    }

    private void addBookingStatusHistory(Booking booking, BookingStatusEnum bookingStatus) {
        BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
        bookingStatusHistory.setBooking(booking);
        bookingStatusHistory.setBookingStatus(bookingStatus);
        bookingStatusHistory.setCreateAt(LocalDateTime.now());
        bookingStatusHistoryRepository.save(bookingStatusHistory);
    }

    private void addBookingEmployee(Booking booking, Integer epmId) {
        User employee = findAccount(epmId, EMPLOYEE.name());

        BookingDetailHelper bookingDetailHelper = new BookingDetailHelper();
//        bookingDetailHelper.setBooking(booking);
//        bookingDetailHelper.setEmployee(employee);
        bookingEmployeeRepository.save(bookingDetailHelper);
    }

    private User findAccount(int userId, String role) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(role + " is not exist"));
    }

    private User findAccount(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    private BookingDetailHelper findEmployeeBooking(int userId) {
        return bookingEmployeeRepository.findTopByEmployeeUserIdOrderByBookingEmpIdDesc(userId)
                .orElseThrow(() -> new NotFoundException("Employee is not exist"));
    }

    private Booking findBookingById(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    private Unit findJobUnit(Integer jobUnitId) {
        return unitRepository.findById(jobUnitId)
                .orElseThrow(() -> new NotFoundException("Job Unit is not exist"));
    }

    private RejectionReason findReject(int rejectId) {
        return rejectionReasonRepository.findById(rejectId)
                .orElseThrow(() -> new NotFoundException("Reject Reason is not exist"));
    }
}
