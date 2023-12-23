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

import java.util.ArrayList;
import java.util.List;

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
                .body(new ResponseObject(HttpStatus.OK.toString(), "All System Parameter", systemParameterRepository.findAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> updateSystemParameter(List<UpdateSystemParameter> systemParameter) {
        try {
            List<SystemParameter> systemParametersForUpdate = new ArrayList<>();
            for (UpdateSystemParameter updateSystemParameter : systemParameter) {
                SystemParameter systemParameterForUpdate = findSystemParameter(updateSystemParameter.getParameterId());
                systemParameterForUpdate.setUpdateAt(Utils.getLocalDateTimeNow());
                systemParameterForUpdate.setParameterValue(updateSystemParameter.getParameterValue());
                String[] versionParts = systemParameterForUpdate.getUpdateVersion().split("\\.");
                int patchVersion = Integer.parseInt(versionParts[1]);
                String version = versionParts[0] + "." + ++patchVersion;
                systemParameterForUpdate.setUpdateVersion(version);
                SystemParameter update = modelMapper.map(systemParameterForUpdate, SystemParameter.class);
                systemParametersForUpdate.add(update);
            }
            systemParameterRepository.saveAll(systemParametersForUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update SystemParameter Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
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
