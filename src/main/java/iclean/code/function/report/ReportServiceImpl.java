package iclean.code.function.report;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.AddReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.enumjava.Role;
import iclean.code.data.repository.BookingRepository;
import iclean.code.data.repository.ReportRepository;
import iclean.code.data.repository.ReportTypeRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.report.service.ReportService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReportTypeRepository reportTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Override
////    public ResponseEntity<ResponseObject> getAllReportAsAdminOrManager(Pageable pageable) {
////        Page<Report> reportPageable = reportRepository.findAllAsAdminOrManager(pageable);
////        PageResponseObject pageResponseObject = Utils.convertToPageResponse(reportPageable);
////
////        return ResponseEntity.status(HttpStatus.OK)
////                .body(new ResponseObject(HttpStatus.OK.toString(), "All Report ", pageResponseObject));
////    }

    @Override
    public ResponseEntity<ResponseObject> getAllReport(Integer userId, Pageable pageable) {
        Page<Report> reportPageable = null;
        User user = findUser(userId);
        if(Objects.equals(Role.RENTER.toString(),user.getRole().getTitle().toUpperCase())){
            reportPageable = reportRepository.finAllReportAsRenter(userId, pageable);
        } else if(Objects.equals(Role.EMPLOYEE.toString(),user.getRole().getTitle().toUpperCase())){
            reportPageable = reportRepository.finAllReportAsStaff(userId, pageable);
        } else {
            reportPageable = reportRepository.findAllAsAdminOrManager(pageable);
        }
        PageResponseObject pageResponseObject = Utils.convertToPageResponse(reportPageable, null);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Report ", pageResponseObject));
    }

    @Override
    public ResponseEntity<ResponseObject> getReportById(Integer reportId, Integer userId) {
        try {
            User user = findUser(userId);
            Report report = findReport(reportId);
            if(Objects.equals(Role.RENTER.toString(),user.getRole().getTitle().toUpperCase())){
                if(!Objects.equals(user.getUserId(), report.getBooking().getRenter().getUserId())){
                    throw new UserNotHavePermissionException();
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString()
                                , "Report", reportRepository.findById(reportId)));
            } else if (Objects.equals(Role.EMPLOYEE.toString(),user.getRole().getTitle().toUpperCase())) {
                if(!Objects.equals(user.getUserId(), report.getBooking().getEmployee().getUserId())){
                    throw new UserNotHavePermissionException();
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString()
                                , "Report", reportRepository.findById(reportId)));
            }
//            if (reportRepository.findById(reportId).isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
//                                , "Report", "Report is not exist"));
//            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Report type", reportRepository.findById(reportId)));
        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addReport(AddReportRequest reportRequest) {
        try {
            if (reportRepository.findReportByBookingBookingId(reportRequest.getBookingId()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                                , "Booking already have report!", null));
            }
            Report report = mappingReportForCreate(reportRequest);
            reportRepository.save(report);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Report Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateReport(int reportId, UpdateReportRequest reportRequest) {
        try {
            Report report = mappingReportForUpdate(reportId, reportRequest);
            reportRepository.save(report);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Report Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteReport(int reportId) {
        try {
            Report report = findReport(reportId);
            reportRepository.delete(report);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete Report Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }


    private Report mappingReportForCreate(AddReportRequest request) {
        Booking optionalBooking = finBooking(request.getBookingId());
        ReportType optionalReportType = findReportType(request.getReportTypeId());

        Report report = modelMapper.map(request, Report.class);
        report.setDetail(request.getDetail());
        report.setReportStatus(request.getReportStatus());
        report.setCreateAt(Utils.getDateTimeNow());
        report.setBooking(optionalBooking);
        report.setReportType(optionalReportType);

        return report;
    }

    private Report mappingReportForUpdate(int reportId, UpdateReportRequest request) {

        Report optionalReport = findReport(reportId);

        optionalReport.setSolution(request.getSolution());
        optionalReport.setRefundPercent(request.getRefundPercent());
        optionalReport.setReportStatus(request.getReportStatus());
        optionalReport.setRefund(request.getRefund());
        optionalReport.setProcessAt(Utils.getDateTimeNow());


        return modelMapper.map(optionalReport, Report.class);
    }

    private Report findReport(int reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report is not exist"));
    }

    private ReportType findReportType(int reportTypeId) {
        return reportTypeRepository.findById(reportTypeId)
                .orElseThrow(() -> new NotFoundException("Report type is not exist"));
    }

    private Booking finBooking(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }

    private User findUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }
}
