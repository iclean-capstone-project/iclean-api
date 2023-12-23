package iclean.code.function.feedback.service.impl;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.domain.Service;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.feedback.CreateFeedbackDto;
import iclean.code.data.dto.request.feedback.FeedbackRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.feedback.GetDetailHelperResponse;
import iclean.code.data.dto.response.feedback.GetFeedbackResponse;
import iclean.code.data.dto.response.feedback.PointFeedbackOfHelper;
import iclean.code.data.enumjava.RoleEnum;
import iclean.code.data.repository.BookingDetailRepository;
import iclean.code.data.repository.ServiceRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.feedback.service.FeedbackService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Log4j2
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    BookingDetailRepository bookingDetailRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getFeedbacks(Integer helperId, Integer serviceId, Pageable pageable) {
        try {
            Page<BookingDetail> bookingDetails = bookingDetailRepository.findByServiceIdAndHelperId(helperId, serviceId, pageable);
            List<GetFeedbackResponse> dtoList = bookingDetails
                    .stream()
                    .map(feedback -> modelMapper.map(feedback, GetFeedbackResponse.class))
                    .collect(Collectors.toList());

            PageResponseObject pageResponseObject = Utils.convertToPageResponse(bookingDetails, dtoList);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Feedback List", pageResponseObject));

        } catch (Exception e) {
            log.error(e.toString());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }
    private BookingDetail findBookingDetail(Integer id) {
        return bookingDetailRepository.findById(id).orElseThrow(() -> new NotFoundException("Feedback is not found"));
    }

    private User findUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User is not found"));
    }
    private boolean isPermission(Integer userId, BookingDetail bookingDetail) throws UserNotHavePermissionException {
        if (!Objects.equals(bookingDetail.getBooking().getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
        return true;
    }

    @Override
    public ResponseEntity<ResponseObject> deleteFeedback(Integer id, Integer userId) {
        try {
            BookingDetail bookingDetail = findBookingDetail(id);
            if (isPermission(userId, bookingDetail)) {
                CreateFeedbackDto dto = new CreateFeedbackDto();
                modelMapper.map(dto, bookingDetail);
                bookingDetailRepository.save(bookingDetail);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Feedback Successfully!", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getDetailOfHelper(Integer helperId, Integer serviceId) {
        try {
            User user = findUser(helperId);
            Service service = findServiceById(serviceId);
            PointFeedbackOfHelper pointFeedbackOfHelper = getDetailOfHelperFunction(helperId, serviceId);
            GetDetailHelperResponse response = modelMapper.map(user, GetDetailHelperResponse.class);
            modelMapper.map(service, response);
            response.setAvgRate(pointFeedbackOfHelper.getRate());
            response.setNumberOfFeedback(pointFeedbackOfHelper.getNumberOfFeedback());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Detail Helper", response));
        } catch (Exception e) {
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

    private Service findServiceById(Integer serviceId) {
        return serviceRepository.findById(serviceId).orElseThrow(() ->
                new NotFoundException("Service is not exist!"));
    }

    @Override
    public PointFeedbackOfHelper getDetailOfHelperFunction(Integer userId, Integer serviceId) {
        try {
            PointFeedbackOfHelper response = bookingDetailRepository.findPointByHelperId(userId, serviceId);
            if (Objects.isNull(response) || response.getNumberOfFeedback() == 0L) {
                return new PointFeedbackOfHelper(5.0D, 0L);
            }
            return response;
        } catch (Exception e) {
            return new PointFeedbackOfHelper(5D, 0L);
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createAndUpdateFeedback(Integer id, FeedbackRequest request, Integer userId) {
        try {
            BookingDetail bookingDetail = findBookingDetail(id);
            modelMapper.map(request, bookingDetail);
            bookingDetail.setFeedbackTime(Utils.getLocalDateTimeNow());
            if (isPermission(userId, bookingDetail)) {
                bookingDetailRepository.save(bookingDetail);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Address Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
}
