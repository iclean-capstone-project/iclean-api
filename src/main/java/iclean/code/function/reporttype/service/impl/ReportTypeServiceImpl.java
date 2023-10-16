package iclean.code.function.reporttype.service.impl;

import iclean.code.data.domain.ReportType;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.reporttype.GetReportTypeDTO;
import iclean.code.data.dto.request.reporttype.AddReportTypeRequest;
import iclean.code.data.dto.request.reporttype.UpdateReportTypeRequest;
import iclean.code.data.repository.ReportTypeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.reporttype.service.ReportTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportTypeServiceImpl implements ReportTypeService {

    @Autowired
    private ReportTypeRepository reportTypeRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ResponseEntity<ResponseObject> getAllReportType() {
        try {
            List<ReportType> reportTypes = reportTypeRepository.findAll();
            GetReportTypeDTO reportTypeResponse = modelMapper.map(reportTypes, GetReportTypeDTO.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "All Report type", reportTypeResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }

    }

    @Override
    public ResponseEntity<ResponseObject> getReportTypeById(int reportTypeId) {
        try {
            ReportType reportType = findReportType(reportTypeId);
            GetReportTypeDTO reportTypeResponse = modelMapper.map(reportType, GetReportTypeDTO.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Report type", reportTypeResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addReportType(AddReportTypeRequest reportTypeRequest) {
        try {
            ReportType reportType = modelMapper.map(reportTypeRequest, ReportType.class);
            reportType.setReportDetail(reportTypeRequest.getReportDetail());

            reportTypeRepository.save(reportType);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Report type Successfully!", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateReportType(int reportTypeId, UpdateReportTypeRequest reportTypeRequest) {
        try {
            ReportType reportTypeForUpdate = findReportType(reportTypeId);
            ReportType reportType = modelMapper.map(reportTypeForUpdate, ReportType.class);

            reportType.setReportDetail(reportTypeRequest.getReportDetail());
            reportTypeRepository.save(reportType);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Report type Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteReportType(int reportTypeId) {
        try {
            Optional<ReportType> optionalReportType = reportTypeRepository.findById(reportTypeId);
            if (optionalReportType.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Report type is not exist", null));

            ReportType reportTypeToDelete = optionalReportType.get();
            reportTypeRepository.delete(reportTypeToDelete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete Report type Successfully!", null));

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

    private ReportType findReportType(int reportTypeId) {
        return reportTypeRepository.findById(reportTypeId)
                .orElseThrow(() -> new NotFoundException("Report type is not exist"));
    }
}
