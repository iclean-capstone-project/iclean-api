package iclean.code.function.profile.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.profile.UpdateProfileDto;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<ResponseObject> getProfile(Integer userId);

    ResponseEntity<ResponseObject> updateProfile(Integer userId, UpdateProfileDto updateProfileDto);
}
