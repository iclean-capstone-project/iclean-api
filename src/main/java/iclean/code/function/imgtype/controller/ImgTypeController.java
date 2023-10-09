package iclean.code.function.imgtype.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgType.AddImgTypeRequest;
import iclean.code.data.dto.request.imgType.UpdateImgTypeRequest;
import iclean.code.function.imgtype.service.ImgTypeService;
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
@RequestMapping("api/v1/imgType")
@Tag(name = "Image Type")
public class ImgTypeController {

    @Autowired
    private ImgTypeService imgTypeService;

    @GetMapping
    @Operation(summary = "Get all image type booking", description = "Return all image type information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image Type Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getAllReportType() {
        return imgTypeService.getAllImgType();
    }

    @GetMapping(value = "{imgTypeId}")
    @Operation(summary = "Get image type booking by id", description = "Return image type information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image Type Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("imgTypeId") @Valid int imgTypeId) {
        return imgTypeService.getImgTypeById(imgTypeId);
    }

    @PostMapping
    @Operation(summary = "Create image type booking ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create Image type booking success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> addBookingStatus(@RequestBody @Valid AddImgTypeRequest request) {
        return imgTypeService.addImgType(request);
    }

    @PutMapping(value = "{imgTypeId}")
    @Operation(summary = "Update image type booking ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Image type booking success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("imgTypeId") int imgTypeId,
                                                              @RequestBody @Valid UpdateImgTypeRequest request) {
        return imgTypeService.updateImgType(imgTypeId, request);
    }

    @DeleteMapping(value = "{imgTypeId}")
    @Operation(summary = "Delete image type booking ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Image type booking success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteBookingStatus(@PathVariable("imgTypeId") @Valid int imgTypeId) {
        return imgTypeService.deleteImgType(imgTypeId);
    }
}
