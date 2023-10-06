package iclean.code.function.report.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.AddReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import iclean.code.function.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllReport() {
        return reportService.getAllReport();
    }

    @GetMapping(value = "{reportId}")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("reportId") @Valid int reportId) {
        return reportService.getReportById(reportId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addBookingStatus(@RequestBody @Valid AddReportRequest request) {
        return reportService.addReport(request);
    }

    @PutMapping(value = "{reportId}")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("reportId") int reportId,
                                                              @RequestBody @Valid UpdateReportRequest request) {
        return reportService.updateReport(reportId, request);
    }

    @DeleteMapping(value = "{reportId}")
    public ResponseEntity<ResponseObject> deleteBookingStatus(@PathVariable("reportId") @Valid int reportId) {
        return reportService.deleteReport(reportId);
    }
}
