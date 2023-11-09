package iclean.code.function.bookingdetailhelper.service.impl;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.domain.BookingDetailHelper;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.bookingdetailhelper.GetBookingDetailHelperResponse;
import iclean.code.data.dto.response.bookingdetailhelper.GetHelpersResponse;
import iclean.code.data.dto.response.feedback.PointFeedbackOfHelper;
import iclean.code.data.repository.BookingDetailHelperRepository;
import iclean.code.data.repository.BookingDetailRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.bookingdetailhelper.service.BookingDetailHelperService;
import iclean.code.function.feedback.service.FeedbackService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BookingDetailHelperServiceImpl implements BookingDetailHelperService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private BookingDetailHelperRepository bookingDetailHelperRepository;
    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Override
    public ResponseEntity<ResponseObject> getHelpersForABooking(Integer detailId, Integer userId) {
        try {
            BookingDetail bookingDetail = findBookingDetail(detailId);
            isPermission(userId, bookingDetail);
            Integer serviceId = bookingDetail.getServiceUnit().getService().getServiceId();
            List<BookingDetailHelper> bookingDetailHelpers = bookingDetailHelperRepository.findByBookingDetailId(detailId);
            List<GetHelpersResponse> dtoList = bookingDetailHelpers
                    .stream()
                    .map(bookingDetailHelper -> {
                        GetHelpersResponse getHelpersResponse = new GetHelpersResponse();
                        User helper = bookingDetailHelper.getServiceRegistration().getHelperInformation().getUser();
                        PointFeedbackOfHelper pointFeedbackOfHelper = feedbackService
                                .getDetailOfHelperFunction(bookingDetailHelper.getServiceRegistration().getHelperInformation().getUser().getUserId(),
                                        bookingDetail.getServiceUnit().getServiceUnitId());
                        getHelpersResponse.setServiceId(serviceId);
                        getHelpersResponse.setHelperId(helper.getUserId());
                        getHelpersResponse.setHelperName(helper.getFullName());
                        getHelpersResponse.setHelperAvatar(helper.getAvatar());
                        getHelpersResponse.setRate(pointFeedbackOfHelper.getRate());
                        getHelpersResponse.setNumberOfFeedback(pointFeedbackOfHelper.getNumberOfFeedback());
                        getHelpersResponse.setPhoneNumber(helper.getPhoneNumber());
                        return getHelpersResponse;
                            }
                    )
                    .collect(Collectors.toList());
            GetBookingDetailHelperResponse response = modelMapper.map(bookingDetail, GetBookingDetailHelperResponse.class);
            response.setHelpers(dtoList);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Get Helpers For Booking", response));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    private void isPermission(Integer userId, BookingDetail booking) throws UserNotHavePermissionException {
        if (!Objects.equals(booking.getBooking().getRenter().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
    }

    private BookingDetail findBookingDetail(int id) {
        return bookingDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking Detail ID %s is not exist!", id)));
    }
}
