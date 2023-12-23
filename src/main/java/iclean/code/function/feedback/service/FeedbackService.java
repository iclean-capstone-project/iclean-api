package iclean.code.function.feedback.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.feedback.FeedbackRequest;
import iclean.code.data.dto.response.feedback.PointFeedbackOfHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface FeedbackService {
    ResponseEntity<ResponseObject> getFeedbacks(Integer helperId, Integer serviceId, Pageable pageable);
    ResponseEntity<ResponseObject> deleteFeedback(Integer id, Integer userId);
    ResponseEntity<ResponseObject> getDetailOfHelper(Integer helperId, Integer serviceId);
    PointFeedbackOfHelper getDetailOfHelperFunction(Integer userId, Integer serviceUnitId);
    ResponseEntity<ResponseObject> createAndUpdateFeedback(Integer id, FeedbackRequest request, Integer userId);
}
