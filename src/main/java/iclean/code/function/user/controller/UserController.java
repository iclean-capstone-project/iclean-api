package iclean.code.function.user.controller;

import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.profile.UserResponse;
import iclean.code.function.user.service.UserService;
import iclean.code.utils.validator.ValidInputList;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@Tag(name = "User API")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Get all user information", description = "Return users information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile Information"),
            @ApiResponse(responseCode = "401", description = "Need Login into the system"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> getUsers(@RequestParam(name = "page", defaultValue = "1") int page,
                                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                                   @RequestParam(name = "role", required = false)
                                                       @ValidInputList(value = "(?i)(admin|manager|renter" +
                                                               "|employee)", message = "Role title is not valid")
                                                       List<String> role,
                                                   @RequestParam(name = "banStatus", required = false)
                                                       Boolean banStatus,
                                                   @RequestParam(name = "phoneName", required = false)
                                                       String phoneName,
                                                   @RequestParam(name = "sort", required = false) @ValidSortFields(value = UserResponse.class) List<String> sortFields) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields, UserResponse.class);
        }
        return userService.getUsers(role, banStatus, phoneName, pageable);
    }

    @PutMapping("{userId}")
    @Operation(summary = "Ban/unban an account of user", description = "Return message success or fail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return message success"),
            @ApiResponse(responseCode = "401", description = "Need Login into the system"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> banUser(@PathVariable Integer userId) {
        return userService.banUser(userId);
    }
}
