package iclean.code.function.booking.service.impl;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.NotificationRequestDto;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingAsRenterRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetBookingHistoryResponse;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.BookingStatusEnum;
import iclean.code.data.enumjava.RoleEnum;
import iclean.code.data.repository.*;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.booking.service.BookingService;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BookingServiceImpl implements BookingService {

    @Autowired
    private FCMService fcmService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private BookingStatusHistoryRepository bookingStatusHistoryRepository;

    @Autowired
    private BookingEmployeeRepository bookingEmployeeRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllBooking(Integer userId, Pageable pageable) {
        Page<Booking> bookings;
        if (RoleEnum.EMPLOYEE.toString().equals(userRepository.findByUserId(userId).getRole().getTitle().toUpperCase())) {
            bookings = bookingRepository.findByStaffId(userId, pageable);
        } else if (RoleEnum.RENTER.toString().equals(userRepository.findByUserId(userId).getRole().getTitle().toUpperCase())) {
            bookings = bookingRepository.findByRenterId(userId, pageable);
        } else
            bookings = bookingRepository.findAllBooking(pageable);

        List<GetBookingResponse> dtoList = bookings
                .stream()
                .map(booking -> modelMapper.map(booking, GetBookingResponse.class))
                .collect(Collectors.toList());

        PageResponseObject pageResponseObject = Utils.convertToPageResponse(bookings, Collections.singletonList(dtoList));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Booking", pageResponseObject));
    }

    @Override
    public ResponseEntity<ResponseObject> getBookingById(Integer bookingId, Integer userId, Pageable pageable) {
        try {
            Booking booking = finBooking(bookingId);
//            if (!Objects.equals(booking.getRenter().getUserId(), userId) ||
//                    !Objects.equals(booking.getEmployee().getUserId(), userId))
//                throw new UserNotHavePermissionException();

            Page<Booking> bookings = bookingRepository.findBookingByBookingId(bookingId, userId, pageable);

            List<GetBookingResponse> dtoList = bookings
                    .stream()
                    .map(bookingMapper -> modelMapper.map(bookingMapper, GetBookingResponse.class))
                    .collect(Collectors.toList());

            PageResponseObject pageResponseObject = Utils.convertToPageResponse(bookings, Collections.singletonList(dtoList));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Booking", pageResponseObject));
        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addBooking(AddBookingRequest request,
                                                     Integer userId) {
        try {
            Booking booking = mappingBookingForCreate(userId, request);
            Booking newBooking = bookingRepository.save(booking);

            BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
            bookingStatusHistory.setBooking(newBooking);
            bookingStatusHistory.setBookingStatus(BookingStatusEnum.NOT_YET);
            bookingStatusHistory.setCreateAt(Utils.getDateTimeNow());
            bookingStatusHistoryRepository.save(bookingStatusHistory);

            //SEND NOTIFICATION
            List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserId(userId);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setTarget(deviceTokens.get(0).getFcmToken());
            notificationRequestDto.setTitle("iClean - Helping Hand Hub Platform");
            notificationRequestDto.setBody("Đơn hàng " + booking.getBookingId() + " của bạn đã được đặt thành công!");

            fcmService.sendPnsToTopic(notificationRequestDto);
            //---------
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Booking Successfully!", null));

        } catch (Exception e) {
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
    public ResponseEntity<ResponseObject> updateStatusBooking(Integer bookingId,
                                                              Integer userId,
                                                              UpdateStatusBookingRequest request) {
        try {

            Booking bookingForUpdateStatus = new Booking();
            Booking booking = finBooking(bookingId);

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
            } else if (Objects.equals(RoleEnum.EMPLOYEE.name(), userUpdate.getRole().getTitle().toUpperCase())) {
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
            notificationRequestDto.setTarget(deviceTokens.get(0).getFcmToken());
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
            Booking booking = finBooking(bookingId);

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
            notificationRequestDto.setTarget(deviceTokens.get(0).getFcmToken());
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
    public ResponseEntity<ResponseObject> deleteBooking(int bookingId) {
        return null;
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

    private Booking mappingBookingForCreate(Integer userId,
                                            AddBookingRequest request) {

        User optionalRenter = findAccount(userId, RoleEnum.RENTER.name());
//        User optionalStaff = findAccount(request.getEmployeeId(), Role.EMPLOYEE.name());
        Unit unit = findJobUnit(request.getJobUnitId());

        Booking booking = modelMapper.map(request, Booking.class);
        booking.setRenter(optionalRenter);
//        booking.setEmployee(optionalStaff);
//        booking.setUnit(unit);
        booking.setOrderDate(Utils.getDateTimeNow());
        booking.setRequestCount(1);

        return booking;
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

            User employee = findAccount(empId, RoleEnum.EMPLOYEE.name());
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
        User employee = findAccount(epmId, RoleEnum.EMPLOYEE.name());

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

    private Booking finBooking(int bookingId) {
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
