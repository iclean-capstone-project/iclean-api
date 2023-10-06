package iclean.code.function.reporttype.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.reporttype.AddReportTypeRequest;
import iclean.code.data.dto.request.reporttype.UpdateReportTypeRequest;
import iclean.code.function.reporttype.service.ReportTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/reportType")
public class ReportTypeController {
    @Autowired
    private ReportTypeService reportTypeService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllReportType() {
        return reportTypeService.getAllReportType();
    }

    @GetMapping(value = "{reportTypeId}")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("reportTypeId") @Valid int reportTypeId) {
        return reportTypeService.getReportTypeById(reportTypeId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addBookingStatus(@RequestBody @Valid AddReportTypeRequest request) {
        return reportTypeService.addReportType(request);
    }

    @PutMapping(value = "{reportTypeId}")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("reportTypeId") int reportTypeId,
                                                              @RequestBody @Valid UpdateReportTypeRequest request) {
        return reportTypeService.updateReportType(reportTypeId, request);
    }

    @DeleteMapping(value = "{reportTypeId}")
    public ResponseEntity<ResponseObject> deleteBookingStatus(@PathVariable("reportTypeId") @Valid int reportTypeId) {
        return reportTypeService.deleteReportType(reportTypeId);
    }
}
