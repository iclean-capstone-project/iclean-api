package iclean.code.function.systemparameter.service.impl;

import iclean.code.data.domain.SystemParameter;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.systemparameter.UpdateSystemParameter;
import iclean.code.data.repository.SystemParameterRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.systemparameter.service.SystemParameterService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SystemParameterServiceImpl implements SystemParameterService {

    @Autowired
    private SystemParameterRepository systemParameterRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllSystemParameter() {
        if (systemParameterRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "All SystemParameter", "SystemParameter list is empty"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All SystemParameter", systemParameterRepository.findAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> getSystemParameterById(int systemId) {
        try {
            if (systemParameterRepository.findById(systemId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "SystemParameter", "Image booking is not exist"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "SystemParameter", systemParameterRepository.findById(systemId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateSystemParameter(int systemId, UpdateSystemParameter systemParameter) {
        try {
            SystemParameter systemParameterForUpdate = findSystemParameter(systemId);
                    //modelMapper.map(systemParameter, SystemParameter.class);
            systemParameterForUpdate.setUpdateAt(Utils.getDateTimeNow());
            systemParameterForUpdate.setUpdateVersion(systemParameter.getUpdateVersion());

            SystemParameter update = modelMapper.map(systemParameterForUpdate, SystemParameter.class);
            systemParameterRepository.save(update);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update SystemParameter Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }

    private SystemParameter findSystemParameter(int systemId) {
        return systemParameterRepository.findById(systemId)
                .orElseThrow(() -> new NotFoundException("System-Parameter is not exist"));
    }
}
