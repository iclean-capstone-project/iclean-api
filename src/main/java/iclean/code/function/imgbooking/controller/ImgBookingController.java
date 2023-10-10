package iclean.code.function.imgbooking.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgbooking.AddImgBooking;
import iclean.code.data.dto.request.imgbooking.UpdateImgBooking;
import iclean.code.function.imgbooking.service.ImgBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/imgBooking")
@Tag(name = "Image Booking")
public class ImgBookingController {
    @Autowired
    private ImgBookingService imgBookingService;

    @GetMapping
    @Operation(summary = "Get all image booking", description = "Return all image booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getAllImgBooking() {
        return imgBookingService.getAllImgBooking();
    }

    @GetMapping(value = "{imgBookingId}")
    @Operation(summary = "Get image booking by image booking id", description = "Return all Image booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getImgBookingById(@PathVariable("imgBookingId") @Valid int imgBookingId) {
        return imgBookingService.getImgBookingById(imgBookingId);
    }

    @PostMapping
    @Operation(summary = "Add image booking ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create Image Booking success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> addImgBooking(@RequestBody @Valid AddImgBooking request) {
        return imgBookingService.addImgBooking(request);
    }

    @PutMapping(value = "{imgBookingId}")
    @Operation(summary = "Update image booking by Image Booking Id", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Image Booking success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateImgBooking(@PathVariable("imgBookingId") int imgBookingId,
                                                           @RequestBody @Valid UpdateImgBooking request) {
        return imgBookingService.updateImgBooking(imgBookingId, request);
    }

    @DeleteMapping(value = "{imgBookingId}")
    @Operation(summary = "Delete image booking by Image Booking Id", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Image Booking success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteImgBooking(@PathVariable("imgBookingId") @Valid int imgBookingId) {
        return imgBookingService.deleteImgBooking(imgBookingId);
    }
}
