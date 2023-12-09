package iclean.code.function.unit.service.impl;

import iclean.code.data.domain.Unit;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.unit.CreateUnitRequest;
import iclean.code.data.dto.request.unit.UpdateUnitRequest;
import iclean.code.data.dto.response.unit.GetUnitResponse;
import iclean.code.data.enumjava.DeleteStatusEnum;
import iclean.code.data.repository.UnitRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.unit.service.UnitService;
import iclean.code.function.common.service.StorageService;
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
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getUnits() {
        try {
            List<Unit> units = unitRepository.findAll();
            List<GetUnitResponse> dtoList = units
                    .stream()
                    .map(unit -> modelMapper.map(unit, GetUnitResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Units", dtoList));
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
    public ResponseEntity<ResponseObject> createUnit(CreateUnitRequest request) {
        try {
            Unit unit = modelMapper.map(request, Unit.class);

            unitRepository.save(unit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Unit Successfully!", null));
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
    public ResponseEntity<ResponseObject> updateUnit(int id, UpdateUnitRequest request) {
        try {
            Unit unit = findUnitById(id);
            modelMapper.map(request, unit);
            if (!Utils.isNullOrEmpty(request.getIsActive())) {
                unit.setIsDeleted(DeleteStatusEnum.valueOf(request.getIsActive().toUpperCase()).getValue());
            }

            unitRepository.save(unit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Unit Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteUnit(int id) {
        try {
            Unit unit = findUnitById(id);
            unit.setIsDeleted(DeleteStatusEnum.INACTIVE.getValue());
            unitRepository.save(unit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete a Unit Successfully!", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private Unit findUnitById(int id) {
        return unitRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Unit ID %s are not exist", "id")));
    }
}
