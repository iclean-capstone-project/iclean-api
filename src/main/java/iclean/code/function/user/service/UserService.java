package iclean.code.function.user.service;

import iclean.code.data.dto.common.ResponseObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<ResponseObject> getUsers(List<String> role, Boolean banStatus, String phoneName, Pageable pageable);

    ResponseEntity<ResponseObject> banUser(Integer userId);
}
