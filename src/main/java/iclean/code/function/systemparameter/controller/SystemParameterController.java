package iclean.code.function.systemparameter.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.systemparameter.CreateSystemParameter;
import iclean.code.data.dto.request.systemparameter.UpdateSystemParameter;
import iclean.code.function.systemparameter.service.SystemParameterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/systemParameter")
@Tag(name = "System Parameter")
public class SystemParameterController {

    @Autowired
    private SystemParameterService systemParameterService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllSystemParameter() {
        return systemParameterService.getAllSystemParameter();
    }

    @GetMapping(value = "{systemId}")
    public ResponseEntity<ResponseObject> getSystemParameterById(@PathVariable("systemId") @Valid int systemId) {
        return systemParameterService.getSystemParameterById(systemId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addNewSystemParameter(@RequestBody @Valid CreateSystemParameter systemParameter) {
        return systemParameterService.addNewSystemParameter(systemParameter);
    }

    @PutMapping(value = "{systemId}")
    public ResponseEntity<ResponseObject> updateSystemParameter(@PathVariable("systemId") int systemId,
                                                              @RequestBody @Valid UpdateSystemParameter systemParameter) {
        return systemParameterService.updateSystemParameter(systemId, systemParameter);
    }

    @DeleteMapping(value = "{systemId}")
    public ResponseEntity<ResponseObject> deleteSystemParameter(@PathVariable("systemId") @Valid int systemId) {
        return systemParameterService.deleteSystemParameter(systemId);
    }
}
