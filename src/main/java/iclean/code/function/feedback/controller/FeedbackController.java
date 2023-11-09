package iclean.code.function.feedback.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.feedback.FeedbackRequest;
import iclean.code.function.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("api/v1/feedback")
@Tag(name = "Feedback API")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    @Operation(summary = "Get all feedback of a helper", description = "Return all feedback information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> getFeedbacks(@RequestParam Integer helperId,
                                                       @RequestParam Integer serviceId,
                                                       @RequestParam(name = "page", defaultValue = "1") int page,
                                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        return feedbackService.getFeedbacks(helperId, serviceId, pageable);
    }

    @GetMapping("/detail")
    @Operation(summary = "Get all detail of a helper", description = "Return detail of a helper")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> getDetailHelpers(@RequestParam Integer helperId,
                                                           @RequestParam Integer serviceId) {
        return feedbackService.getDetailOfHelper(helperId, serviceId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create or Update a feedback", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a feedback Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createOrUpdateFeedback(@RequestBody @Valid FeedbackRequest request,
                                                        @PathVariable Integer id,
                                                        Authentication authentication) {
        return feedbackService.createAndUpdateFeedback(id, request, JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'manager')")
    @Operation(summary = "Delete a feedback by booking detail id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a feedback Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteFeedback(@PathVariable Integer id,
                                                        Authentication authentication) {
        return feedbackService.deleteFeedback(id, JwtUtils.decodeToAccountId(authentication));
    }

}
