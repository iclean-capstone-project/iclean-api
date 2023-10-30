package iclean.code.function.bookingattachment.service.impl;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.bookingattachment.AddBookingAttachment;
import iclean.code.data.dto.request.bookingattachment.UpdateBookingAttachment;
import iclean.code.data.dto.response.bookingattachment.GetBookingAttachmentDTO;
import iclean.code.data.repository.BookingRepository;
import iclean.code.data.repository.BookingAttachmentRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.bookingattachment.service.BookingAttachmentService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingAttachmentAttachmentServiceImpl implements BookingAttachmentService {

    @Autowired
    private BookingAttachmentRepository bookingAttachmentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllImgBooking() {
        List<BookingAttachment> bookingAttachments = bookingAttachmentRepository.findAll();
        GetBookingAttachmentDTO imgBookingResponse = modelMapper.map(bookingAttachments, GetBookingAttachmentDTO.class);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Image booking", imgBookingResponse));
    }

    @Override
    public ResponseEntity<ResponseObject> getImgBookingById(int imgBookingId) {
        try {
            BookingAttachment bookingAttachment = findImgBooking(imgBookingId);
            GetBookingAttachmentDTO imgBookingResponse = modelMapper.map(bookingAttachment, GetBookingAttachmentDTO.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Image booking", imgBookingResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addImgBooking(AddBookingAttachment request) {
        try {

            BookingAttachment bookingAttachment = mappingImgBookingForCreate(request);
            bookingAttachmentRepository.save(bookingAttachment);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Image booking Successfully!", null));

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
    public ResponseEntity<ResponseObject> updateImgBooking(int imgBookingId, UpdateBookingAttachment request) {
        try {
            BookingAttachment bookingAttachmentForUpdate = findImgBooking(imgBookingId);
            BookingAttachment bookingAttachment = modelMapper.map(bookingAttachmentForUpdate, BookingAttachment.class);

            bookingAttachment.setBookingAttachmentLink(request.getImgBookingLink());

            bookingAttachmentRepository.save(bookingAttachment);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Image type Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteImgBooking(int imgBookingId) {
        try {
            BookingAttachment bookingAttachmentForDelete = findImgBooking(imgBookingId);
            bookingAttachmentRepository.delete(bookingAttachmentForDelete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete Image booking Successfully!", null));

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

    private BookingAttachment findImgBooking(int imgBookingId) {
        return bookingAttachmentRepository.findById(imgBookingId)
                .orElseThrow(() -> new NotFoundException("Image booking is not exist"));
    }

    private Booking finBooking(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    private BookingAttachment mappingImgBookingForCreate(AddBookingAttachment request) {
        Booking optionalBooking = finBooking(request.getBookingId());

        BookingAttachment bookingAttachment = modelMapper.map(request, BookingAttachment.class);
        bookingAttachment.setBookingAttachmentLink(request.getBookingAttachmentLink());
        bookingAttachment.setCreateAt(Utils.getDateTimeNow());
        bookingAttachment.setBooking(optionalBooking);

        return bookingAttachment;
    }
}
