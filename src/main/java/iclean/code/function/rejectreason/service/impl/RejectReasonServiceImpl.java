package iclean.code.function.rejectreason.service.impl;

import iclean.code.data.domain.RejectReason;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.rejectreason.CreateRejectReasonRequestDTO;
import iclean.code.data.dto.request.rejectreason.GetRejectReasonRequestDTO;
import iclean.code.data.dto.request.rejectreason.UpdateRejectReasonRequestDTO;
import iclean.code.data.dto.response.rejectreason.GetRejectReasonResponseDTO;
import iclean.code.data.repository.RejectReasonRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.rejectreason.service.RejectReasonService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RejectReasonServiceImpl implements RejectReasonService {
    @Autowired
    private RejectReasonRepository rejectReasonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getRejectReasons(GetRejectReasonRequestDTO request) {
        try {
            List<RejectReason> rejectReasons = rejectReasonRepository.findAll();
            List<GetRejectReasonResponseDTO> responses = rejectReasons
                    .stream()
                    .map(rejectReason -> modelMapper.map(rejectReason, GetRejectReasonResponseDTO.class))
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
    public ResponseEntity<ResponseObject> getRejectReason(Integer id) {
        try {
            RejectReason rejectReason = findRejectReasonById(id);
            GetRejectReasonResponseDTO responses = modelMapper.map(rejectReason, GetRejectReasonResponseDTO.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Reject Reason Information",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.toString(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createRejectReason(CreateRejectReasonRequestDTO request) {
        try {
            RejectReason rejectReason = modelMapper.map(request, RejectReason.class);
            rejectReasonRepository.save(rejectReason);

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
    public ResponseEntity<ResponseObject> updateRejectReason(Integer id, UpdateRejectReasonRequestDTO request) {
        try {
            RejectReason rejectReason = findRejectReasonById(id);
            rejectReason = modelMapper.map(request, RejectReason.class);
            rejectReasonRepository.save(rejectReason);

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
            RejectReason rejectReason = findRejectReasonById(id);
            rejectReasonRepository.delete(rejectReason);

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
    private RejectReason findRejectReasonById(Integer id) {
        return rejectReasonRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reject Reason ID: %s is not exist", id)));
    }

    private User findUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }
}
