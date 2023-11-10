package iclean.code.function.profile.service.impl;

import iclean.code.data.domain.Address;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.profile.UpdateProfileDto;
import iclean.code.data.dto.response.profile.ProfileUserResponse;
import iclean.code.data.repository.AddressRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.profile.service.ProfileService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    StorageService storageService;
    @Override
    public ResponseEntity<ResponseObject> getProfile(Integer userId) {
        try {
            User user = findUser(userId);
            ProfileUserResponse profileUserResponse = modelMapper.map(user, ProfileUserResponse.class);
            List<Address> addressList = addressRepository.findByUserIdAnAndIsDefault(user.getUserId());
            if (!addressList.isEmpty()) {
                profileUserResponse.setDefaultAddress(addressList.get(0).getDescription());
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Login success!",
                            profileUserResponse));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur.", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateProfile(Integer userId, UpdateProfileDto updateProfileDto) {
        try {
            User user = findUser(userId);
            if (Objects.nonNull(user.getRole())) {
                throw new UserNotHavePermissionException();
            }
            updateProfileDto.setFullName(Utils.convertToTitleCase(updateProfileDto.getFullName()));
            modelMapper.map(updateProfileDto, user);
            user.setDateOfBirth(Utils.convertStringToLocalDate(updateProfileDto.getDateOfBirth()));
            if (Objects.nonNull(updateProfileDto.getFileImage())) {
                String avatar = storageService.uploadFile(updateProfileDto.getFileImage());
                user.setAvatar(avatar);
            }
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Information Successful", null));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur.", null));
        }
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found in the system"));
    }
}
