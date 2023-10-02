package iclean.code.function.rejectreason.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.rejectreason.CreateRejectReasonRequestDTO;
import iclean.code.data.dto.request.rejectreason.GetRejectReasonRequestDTO;
import iclean.code.data.dto.request.rejectreason.UpdateRejectReasonRequestDTO;
import iclean.code.function.rejectreason.service.RejectReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/reject-reason")
@Tag(name = "Reject Reason API")
public class RejectReasonController {
    @Autowired
    private RejectReasonService rejectReasonService;

    @GetMapping
    @Operation(summary = "Get all reject reasons of a user", description = "Return all reject reasons information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New Employees Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getRejectReasons(@RequestParam @Valid GetRejectReasonRequestDTO request) {
        return rejectReasonService.getRejectReasons(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reject reason of a user by id", description = "Return reject reason information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reject Reason Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getRejectReason(@PathVariable Integer id) {
        return rejectReasonService.getRejectReason(id);
    }

    @PostMapping
    @Operation(summary = "Create new reject reason of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Reject Reason Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createRejectReason(@RequestBody @Valid CreateRejectReasonRequestDTO request) {
        return rejectReasonService.createRejectReason(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a reject reason of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a Reject Reason Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateRejectReason(@RequestBody @Valid UpdateRejectReasonRequestDTO request,
                                                                 @PathVariable Integer id) {
        return rejectReasonService.updateRejectReason(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reject reason of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a Reject Reason Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateRejectReason(@PathVariable Integer id) {
        return rejectReasonService.deleteRejectReason(id);
    }

}
