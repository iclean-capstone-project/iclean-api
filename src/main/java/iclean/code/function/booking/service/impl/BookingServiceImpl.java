package iclean.code.function.booking.service.impl;

import iclean.code.data.domain.Booking;
import iclean.code.data.domain.BookingStatus;
import iclean.code.data.domain.Job;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import iclean.code.data.enumjava.BookingStatusEnum;
import iclean.code.data.enumjava.Role;
import iclean.code.data.repository.BookingRepository;
import iclean.code.data.repository.BookingStatusRepository;
import iclean.code.data.repository.JobRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.booking.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllBooking() {
        if (bookingRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "All Booking", "Booking list is empty"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Booking", bookingRepository.findAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> getBookingById(int bookingId) {
        try {
            if (bookingRepository.findById(bookingId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Booking", "Booking is not exist"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Booking", bookingRepository.findById(bookingId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addBooking(AddBookingRequest request) {
        try {
            Booking booking = mappingBookingForCreate(request);
            bookingRepository.save(booking);

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
    public ResponseEntity<ResponseObject> updateStatusBooking(int bookingId, UpdateStatusBookingRequest request) {
        try {
            Booking booking = mappingBookingForUpdateStatus(bookingId, request);
            bookingRepository.save(booking);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Status Booking Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteBooking(int bookingId) {
        return null;
    }

    private Booking mappingBookingForCreate(AddBookingRequest request) {

        User optionalRenter = findAccount(request.getRenterId(), Role.RENTER.name());
        User optionalStaff = findAccount(request.getStaffId(), Role.EMPLOYEE.name());
        Job optionalJob = finJob(request.getJobId());
        BookingStatus optionalBookingStatus = findStatus(BookingStatusEnum.WAITING.getValue());

        Booking booking = modelMapper.map(request, Booking.class);
        booking.setRenter(optionalRenter);
        booking.setStaff(optionalStaff);
        booking.setJob(optionalJob);
        booking.setBookingStatus(optionalBookingStatus);
        booking.setOrderDate(LocalDateTime.now());
        booking.setRequestCount(1);

        return booking;
    }

    private Booking mappingBookingForUpdateStatus(int bookingId, UpdateStatusBookingRequest request) {

        BookingStatus optionalBookingStatus = findStatus(request.getBookingStatusId());
        Booking optionalBooking = finBooking(bookingId);

        optionalBooking.setRequestCount(optionalBooking.getRequestCount() + 1);
        optionalBooking.setBookingStatus(optionalBookingStatus);
        optionalBooking.setUpdateAt(LocalDateTime.now());

        Booking booking = modelMapper.map(optionalBooking, Booking.class);

        if (BookingStatusEnum.IN_PROCESS.getValue() == optionalBookingStatus.getBookingStatusId()) {
            booking.setWorkStart(LocalDateTime.now());

        } else if (BookingStatusEnum.DONE.getValue() == optionalBookingStatus.getBookingStatusId()) {
            booking.setWorkEnd(LocalDateTime.now());
        }
        return booking;
    }

    private User findAccount(int userId, String role) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(role + " is not exist"));
    }

    private Booking finBooking(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    private BookingStatus findStatus(int statusId) {
        return bookingStatusRepository.findById(statusId)
                .orElseThrow(() -> new NotFoundException("Status is not exist"));
    }

    private Job finJob(int jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job is not exist"));
    }
}
