package iclean.code.function.moneypoint.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import iclean.code.data.domain.MoneyPoint;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneypoint.CreateMoneyPoint;
import iclean.code.data.dto.request.moneypoint.UpdateMoneyPoint;
import iclean.code.data.repository.MoneyPointRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.moneypoint.service.MoneyPointService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MoneyPointServiceImpl implements MoneyPointService {
    @Autowired
    private MoneyPointRepository moneyPointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllMoneyPoint() {
        if (moneyPointRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "All Money Point", "Money Point list is empty"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Money Point", moneyPointRepository.findAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> getMoneyPointById(int moneyPointById) {
        try {
            if (moneyPointRepository.findById(moneyPointById).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Money Point", "Money Point is not exist"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Money Point", moneyPointRepository.findById(moneyPointById)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getMoneyPointByUserId(int userId) {
        try {
            if (moneyPointRepository.findMoneyPointByUserUserId(userId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Money Point", "Money Point is not exist"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Money Point", moneyPointRepository.findMoneyPointByUserUserId(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addNewMoneyPoint(CreateMoneyPoint moneyPoint) {
        try {
            MoneyPoint moneyPointForCreate = modelMapper.map(moneyPoint, MoneyPoint.class);
            moneyPointForCreate.setCurrentMoney(moneyPoint.getCurrentMoney());
            moneyPointForCreate.setCurrentPoint(moneyPoint.getCurrentPoint());

            User userForCreate = finUser(moneyPoint.getUserId());
            moneyPointForCreate.setUser(userForCreate);
            moneyPointRepository.save(moneyPointForCreate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create MoneyPoint Successfully!", null));

        }  catch (Exception e) {
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
    public ResponseEntity<ResponseObject> updateMoneyPointByUserId(int userId, UpdateMoneyPoint moneyPoint) {
        try {
            MoneyPoint moneyPointForUpdate = mappingMoneyPointForUpdate(userId, moneyPoint);

            moneyPointRepository.save(moneyPointForUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update MoneyPoint Successfully!", null));


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
    public ResponseEntity<ResponseObject> deleteMoneyPoint(int moneyPointById) {
        try {
            MoneyPoint moneyPointForDelete = finMoneyPoint(moneyPointById);
            moneyPointRepository.delete(moneyPointForDelete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete MoneyPoint Successfully!", null));

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

    private MoneyPoint mappingMoneyPointForUpdate(int userId, UpdateMoneyPoint moneyPoint) {

        MoneyPoint optionalMoneyPoint = finMoneyPointByUserId(userId);

        optionalMoneyPoint.setCurrentMoney(moneyPoint.getCurrentMoney());
        optionalMoneyPoint.setCurrentPoint(moneyPoint.getCurrentPoint());
        optionalMoneyPoint.setUpdateAt(Utils.getDateTimeNow());


        return modelMapper.map(optionalMoneyPoint, MoneyPoint.class);
    }

    private User finUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    private MoneyPoint finMoneyPoint(int moneyPointId) {
        return moneyPointRepository.findById(moneyPointId)
                .orElseThrow(() -> new NotFoundException("MoneyPoint is not exist"));
    }

    private MoneyPoint finMoneyPointByUserId(int userId) {
        return moneyPointRepository.findMoneyPointByUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("MoneyPoint is not exist"));
    }
}
