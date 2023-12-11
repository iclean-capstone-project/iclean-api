package iclean.code.function.systemparameter.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.systemparameter.UpdateSystemParameter;
import iclean.code.function.systemparameter.service.SystemParameterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/system-parameter")
@Tag(name = "System Parameter API")
public class SystemParameterController {

    @Autowired
    private SystemParameterService systemParameterService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getAllSystemParameter() {
        return systemParameterService.getAllSystemParameter();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateSystemParameter(@RequestBody @Valid UpdateSystemParameter systemParameter) {
        return systemParameterService.updateSystemParameter(systemParameter);
    }
}
