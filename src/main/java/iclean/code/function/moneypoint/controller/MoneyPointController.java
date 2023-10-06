package iclean.code.function.moneypoint.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneypoint.CreateMoneyPoint;
import iclean.code.data.dto.request.moneypoint.UpdateMoneyPoint;
import iclean.code.function.moneypoint.service.MoneyPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/moneyPoint")
public class MoneyPointController {

    @Autowired
    private MoneyPointService moneyPointService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllMoneyPoint() {
        return moneyPointService.getAllMoneyPoint();
    }

    @GetMapping(value = "{moneyPointId}")
    public ResponseEntity<ResponseObject> getMoneyPointById(@PathVariable("moneyPointId") @Valid int moneyPointId) {
        return moneyPointService.getMoneyPointById(moneyPointId);
    }

    @GetMapping(value = "{userId}")
    public ResponseEntity<ResponseObject> getMoneyPointByUserId(@PathVariable("userId") @Valid int userId) {
        return moneyPointService.getMoneyPointByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addNewMoneyPoint(@RequestBody @Valid CreateMoneyPoint moneyPoint) {
        return moneyPointService.addNewMoneyPoint(moneyPoint);
    }

    @PutMapping(value = "{userId}")
    public ResponseEntity<ResponseObject> updateMoneyPointByUserId(@PathVariable("userId") int userId,
                                                              @RequestBody @Valid UpdateMoneyPoint moneyPoint) {
        return moneyPointService.updateMoneyPointByUserId(userId, moneyPoint);
    }

    @DeleteMapping(value = "{moneyPointById}")
    public ResponseEntity<ResponseObject> deleteMoneyPoint(@PathVariable("moneyPointById") @Valid int moneyPointById) {
        return moneyPointService.deleteMoneyPoint(moneyPointById);
    }
}
