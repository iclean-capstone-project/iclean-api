package iclean.code.function.booking.service.impl;

import iclean.code.config.MessageVariable;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.Position;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.booking.*;
import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.booking.GetBookingResponseForHelper;
import iclean.code.data.dto.response.booking.GetCartResponseDetail;
import iclean.code.data.dto.response.bookingdetail.QRCodeResponse;
import iclean.code.data.enumjava.*;
import iclean.code.data.repository.*;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.booking.service.BookingService;
import iclean.code.function.serviceprice.service.ServicePriceService;
import iclean.code.service.FCMService;
import iclean.code.service.GoogleMapService;
import iclean.code.service.QRCodeService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static iclean.code.data.enumjava.RoleEnum.EMPLOYEE;

@Service
@Log4j2
public class BookingServiceImpl implements BookingService {

    @Autowired
    private FCMService fcmService;

    @Value("${iclean.app.max.distance.length}")
    private Double maxDistance;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

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
    private ServicePriceService servicePriceService;

    @Autowired
    GoogleMapService googleMapService;

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
            Address address = null;
            List<Address> addresses = addressRepository.findByUserIdAnAndIsDefault(userId);
            if (!addresses.isEmpty()) {
                address = addresses.get(0);
            }
            List<BookingDetail> bookings = bookingDetailRepository.findBookingDetailByStatusAndNoUserId(BookingStatusEnum.APPROVED, userId);
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
                        new Position(address.getLongitude(), address.getLatitude()), maxDistance);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Booking History Response!", response));
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
            Double priceHelper = servicePriceService
                    .getServiceHelperPrice(new GetServicePriceRequest(request.getServiceUnitId(),
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
    public ResponseEntity<ResponseObject> getBookingHistory(int userId, String status, Pageable pageable) {
        try {
            BookingStatusEnum bookingStatusEnum = BookingStatusEnum.valueOf(status.toUpperCase());
            Page<Booking> bookings = bookingRepository.findBookingHistoryByUserId(userId, bookingStatusEnum, pageable);
            PageResponseObject pageResponseObject = getResponseObjectResponseEntity(bookings);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Get Booking History!", pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }

    }

    @Override
    public ResponseEntity<ResponseObject> acceptBookingForHelper(CreateBookingHelperRequest request, Integer userId) {
        try {
            Booking booking = bookingRepository.findBookingByBookingDetailAndStatus(request.getBookingDetailId(), BookingStatusEnum.APPROVED);
            if (booking == null) throw new NotFoundException("The booking cannot do this action");
            BookingDetail bookingDetail = findBookingDetail(request.getBookingDetailId());
            ServiceRegistration serviceRegistration = serviceRegistrationRepository
                    .findByServiceIdAndUserId(bookingDetail.getServiceUnit().getService().getServiceId(), userId,
                            ServiceHelperStatusEnum.ACTIVE);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private BookingDetail findBookingDetailByStatus(Integer detailId, BookingStatusEnum statusEnum) {
        return bookingDetailRepository.findByBookingDetailIdAndBookingStatus(detailId, statusEnum)
                .orElseThrow(() -> new NotFoundException("Booking Detail is not found"));
    }
    @Override
    public ResponseEntity<ResponseObject> generateQrCode(Integer renterId, Integer detailId) {
        try {
            BookingDetail bookingDetail = findBookingDetailByStatus(detailId, BookingStatusEnum.WAITING);
            isPermission(renterId, bookingDetail);
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

    private void isPermissionForHelper(Integer userId, BookingDetail bookingDetail) throws UserNotHavePermissionException {
        List<BookingDetailHelper> helpers = bookingDetailHelperRepository.findByBookingDetailIdAndActive(bookingDetail.getBookingDetailId(),
                BookingDetailHelperStatusEnum.ACTIVE);
        User helper = helpers.get(0).getServiceRegistration().getHelperInformation().getUser();
        if (!Objects.equals(helper.getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
    }

    private PageResponseObject getResponseObjectResponseEntity(Page<Booking> bookings) {
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
        return Utils.convertToPageResponse(bookings, Collections.singletonList(dtoList));
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

    private BookingDetail findBookingDetail(int id) {
        return bookingDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking Detail ID %s is not exist!", id)));
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
