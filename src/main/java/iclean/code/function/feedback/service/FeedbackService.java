package iclean.code.function.feedback.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.feedback.FeedbackRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface FeedbackService {
    ResponseEntity<ResponseObject> getFeedbacks(Integer serviceRegistrationId, Pageable pageable);

    ResponseEntity<ResponseObject> deleteFeedback(Integer id, Integer userId);

    ResponseEntity<ResponseObject> createAndUpdateFeedback(Integer id, FeedbackRequest request, Integer userId);
}
