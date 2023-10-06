package iclean.code.function.imgbooking.service.impl;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgbooking.AddImgBooking;
import iclean.code.data.dto.request.imgbooking.UpdateImgBooking;
import iclean.code.data.repository.BookingRepository;
import iclean.code.data.repository.ImgBookingRepository;
import iclean.code.data.repository.ImgTypeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.imgbooking.service.ImgBookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ImgBookingServiceImpl implements ImgBookingService {

    @Autowired
    private ImgBookingRepository imgBookingRepository;

    @Autowired
    private ImgTypeRepository imgTypeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllImgBooking() {
        if (imgBookingRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "All Image booking", "Image booking list is empty"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Image booking", imgBookingRepository.findAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> getImgBookingById(int imgBookingId) {
        try {
            if (imgBookingRepository.findById(imgBookingId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Image booking", "Image booking is not exist"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Image booking", imgBookingRepository.findById(imgBookingId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addImgBooking(AddImgBooking request) {
        try {

            ImgBooking imgBooking = mappingImgBookingForCreate(request);
            imgBookingRepository.save(imgBooking);

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
    public ResponseEntity<ResponseObject> updateImgBooking(int imgBookingId, UpdateImgBooking request) {
        try {
            ImgBooking imgBookingForUpdate = findImgBooking(imgBookingId);
            ImgBooking imgBooking = modelMapper.map(imgBookingForUpdate, ImgBooking.class);

            ImgType imgTypeForUpdate = findImgType(request.getImgTypeId());
            imgBooking.setImgBookingLink(request.getImgBookingLink());
            imgBooking.setImgType(imgTypeForUpdate);

            imgBookingRepository.save(imgBooking);

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
            ImgBooking imgBookingForDelete = findImgBooking(imgBookingId);
            imgBookingRepository.delete(imgBookingForDelete);
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

    private ImgBooking findImgBooking(int imgBookingId) {
        return imgBookingRepository.findById(imgBookingId)
                .orElseThrow(() -> new NotFoundException("Image booking is not exist"));
    }

    private ImgType findImgType(int imgTypeId) {
        return imgTypeRepository.findById(imgTypeId)
                .orElseThrow(() -> new NotFoundException("Image type is not exist"));
    }

    private Booking finBooking(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    private ImgBooking mappingImgBookingForCreate(AddImgBooking request) {
        Booking optionalBooking = finBooking(request.getBookingId());
        ImgType optionalImgType = findImgType(request.getImgTypeId());

        ImgBooking imgBooking = modelMapper.map(request, ImgBooking.class);
        imgBooking.setImgBookingLink(request.getImgBookingLink());
        imgBooking.setImgType(optionalImgType);
        imgBooking.setCreateAt(LocalDateTime.now());
        imgBooking.setBooking(optionalBooking);

        return imgBooking;
    }
}
