package iclean.code.function.report;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.CreateReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.report.GetReportResponseAsManager;
import iclean.code.data.dto.response.report.GetReportResponseDetail;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import iclean.code.data.enumjava.BookingStatusEnum;
import iclean.code.data.enumjava.OptionProcessReportEnum;
import iclean.code.data.enumjava.ReportStatusEnum;
import iclean.code.data.repository.*;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.report.service.ReportService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingStatusHistoryRepository bookingStatusHistoryRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private ReportTypeRepository reportTypeRepository;

    @Autowired
    private BookingAttachmentRepository bookingAttachmentRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getReports(Integer userId, String renterName, Boolean displayAll, Pageable pageable) {
        try {
            Page<Report> reports;
            if (!displayAll) {
                reports = reportRepository.findReportsAsManager(userId, Utils.removeAccentMarksForSearching(renterName), pageable);
            } else {
                reports = reportRepository.findAllReportByRenterName(Utils.removeAccentMarksForSearching(renterName), pageable);
            }
            List<GetReportResponseAsManager> data = reports
                    .stream()
                    .map(report -> modelMapper.map(report, GetReportResponseAsManager.class))
                    .collect(Collectors.toList());
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(reports, data);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Report ", pageResponseObject));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getReportById(Integer reportId) {
        try {
            Report report = findReport(reportId);
            GetReportResponseDetail response = modelMapper.map(report, GetReportResponseDetail.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Report type", response));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createReport(CreateReportRequest reportRequest, Integer renterId) {
        try {
            if (reportRepository.findReportByBookingBookingId(reportRequest.getBookingId()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                                , "Booking already have report!", null));
            }

            //Add new record REPORTED for Booking History
            Booking booking = findBooking(reportRequest.getBookingId());
            setStatusOfBookingStatusHistory(booking, BookingStatusEnum.REPORTED);

            if (!Objects.equals(booking.getRenter().getUserId(), renterId))
                throw new UserNotHavePermissionException("User cannot do this action");
            List<String> images = new ArrayList<>(Collections.emptyList());
            for (MultipartFile file :
                    reportRequest.getFiles()) {
                images.add(storageService.uploadFile(file));
            }
            List<BookingAttachment> bookingAttachments = new ArrayList<>();
            for (String imageLink :
                    images) {
                BookingAttachment bookingAttachment = new BookingAttachment();
                bookingAttachment.setBookingAttachmentLink(imageLink);
                bookingAttachment.setBooking(booking);
                bookingAttachments.add(bookingAttachment);
            }
            if (!bookingAttachments.isEmpty()) {
                bookingAttachmentRepository.saveAll(bookingAttachments);
            }
            Report report = mappingReportForCreate(reportRequest);
            reportRepository.save(report);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Report Successfully!", null));

        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateReport(int reportId, UpdateReportRequest reportRequest) {
        try {
            Report report = mappingReportForUpdate(reportId, reportRequest);
            reportRepository.save(report);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Report Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteReport(int reportId) {
        try {
            Report report = findReport(reportId);
            report.setReportStatus(ReportStatusEnum.REJECTED);

            //Implement notification and send mail, other logic

            reportRepository.save(report);

            //Add new record FINISHED for Booking History
            Booking booking = findBooking(report.getBooking().getBookingId());
            setStatusOfBookingStatusHistory(booking, BookingStatusEnum.FINISHED);

            //Add new record FINISHED for Booking Detail
            List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBookingBookingId(booking.getBookingId());
            if (bookingDetails.isEmpty()) {
                throw new NotFoundException("Booking have not booking details");
            }
            for (BookingDetail bookingDetail :
                    bookingDetails) {
                bookingDetail.setBookingDetailStatusEnum(BookingDetailStatusEnum.FINISHED);
                bookingDetailRepository.save(bookingDetail);
            }

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

    private void setStatusOfBookingStatusHistory(Booking booking, BookingStatusEnum statusEnum) {
        BookingStatusHistory bookingStatusHistory = new BookingStatusHistory();
        bookingStatusHistory.setBookingStatus(statusEnum);
        bookingStatusHistory.setBooking(booking);
        bookingStatusHistory.setCreateAt(Utils.getDateTimeNow());
        bookingStatusHistoryRepository.save(bookingStatusHistory);
    }

    private Report mappingReportForCreate(CreateReportRequest request) {
        Booking optionalBooking = findBooking(request.getBookingId());
        ReportType optionalReportType = findReportType(request.getReportTypeId());
        Report report = modelMapper.map(request, Report.class);
        report.setDetail(request.getDetail());
        report.setReportStatus(ReportStatusEnum.PROCESSING);
        report.setCreateAt(Utils.getDateTimeNow());
        report.setBooking(optionalBooking);
        report.setReportType(optionalReportType);

        return report;
    }

    private Report mappingReportForUpdate(int reportId, UpdateReportRequest request) {
        Report optionalReport = findReport(reportId);
        optionalReport.setSolution(request.getSolution());
        optionalReport.setOption(OptionProcessReportEnum.valueOf(request.getOption().toUpperCase()));
        optionalReport.setReportStatus(ReportStatusEnum.PROCESSED);
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

    private Booking findBooking(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not exist"));
    }
}
