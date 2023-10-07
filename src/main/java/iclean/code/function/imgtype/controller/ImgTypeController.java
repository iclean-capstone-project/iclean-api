package iclean.code.function.imgtype.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgtype.AddImgTypeRequest;
import iclean.code.data.dto.request.imgtype.UpdateImgTypeRequest;
import iclean.code.function.imgtype.service.ImgTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/imgType")
public class ImgTypeController {

    @Autowired
    private ImgTypeService imgTypeService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllReportType() {
        return imgTypeService.getAllImgType();
    }

    @GetMapping(value = "{imgTypeId}")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("imgTypeId") @Valid int imgTypeId) {
        return imgTypeService.getImgTypeById(imgTypeId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addBookingStatus(@RequestBody @Valid AddImgTypeRequest request) {
        return imgTypeService.addImgType(request);
    }

    @PutMapping(value = "{imgTypeId}")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("imgTypeId") int imgTypeId,
                                                              @RequestBody @Valid UpdateImgTypeRequest request) {
        return imgTypeService.updateImgType(imgTypeId, request);
    }

    @DeleteMapping(value = "{imgTypeId}")
    public ResponseEntity<ResponseObject> deleteBookingStatus(@PathVariable("imgTypeId") @Valid int imgTypeId) {
        return imgTypeService.deleteImgType(imgTypeId);
    }
}
