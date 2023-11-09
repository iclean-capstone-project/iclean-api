package iclean.code.function.rejectionreason.service.impl;

import iclean.code.data.domain.RejectionReason;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.rejectionreason.CreateRejectionReasonRequestDTO;
import iclean.code.data.dto.request.rejectionreason.UpdateRejectionReasonRequestDTO;
import iclean.code.data.dto.response.rejectionreason.GetRejectionReasonResponseDTO;
import iclean.code.data.repository.RejectionReasonRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.rejectionreason.service.RejectReasonService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RejectReasonServiceImpl implements RejectReasonService {
    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getRejectionReasons() {
        try {
            List<RejectionReason> rejectionReasons = rejectionReasonRepository.findAll();
            List<GetRejectionReasonResponseDTO> responses = rejectionReasons
                    .stream()
                    .map(rejectionReason -> modelMapper.map(rejectionReason, GetRejectionReasonResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Reject Reason List",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createRejectReason(CreateRejectionReasonRequestDTO request) {
        try {
            RejectionReason rejectionReason = modelMapper.map(request, RejectionReason.class);
            rejectionReason.setCreateAt(Utils.getDateTimeNow());
            rejectionReasonRepository.save(rejectionReason);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Reject Reason Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateRejectReason(Integer id, UpdateRejectionReasonRequestDTO request) {
        try {
            RejectionReason rejectionReason = findRejectReasonById(id);
            modelMapper.map(request, rejectionReason);
            rejectionReasonRepository.save(rejectionReason);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Reject Reason Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteRejectReason(Integer id) {
        try {
            RejectionReason rejectionReason = findRejectReasonById(id);
            rejectionReasonRepository.delete(rejectionReason);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Reject Reason Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    private RejectionReason findRejectReasonById(Integer id) {
        return rejectionReasonRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reject Reason ID: %s is not exist", id)));
    }
}
