package iclean.code.function.user.service.impl;

import iclean.code.data.domain.Address;
import iclean.code.data.domain.Booking;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.profile.UserResponse;
import iclean.code.data.enumjava.BookingStatusEnum;
import iclean.code.data.enumjava.RoleEnum;
import iclean.code.data.repository.AddressRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.user.service.UserService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getUsers(List<String> role, Boolean banStatus, String phoneName, Pageable pageable) {
        try {
            phoneName = Utils.removeAccentMarksForSearching(phoneName);
            Page<User> users;
            if (role != null && !role.isEmpty()) {
                if (banStatus != null && banStatus) {
                    users = userRepository.findAllByRoleAndBanStatus(role, true, phoneName, pageable);
                } else if (banStatus == null) {
                    users = userRepository.findAllByRole(role, phoneName, pageable);
                } else {
                    users = userRepository.findAllByRoleAndNotBan(role, phoneName, pageable);
                }
            } else {
                if (banStatus != null && banStatus) {
                    users = userRepository.findAllByBanStatus(banStatus, phoneName, pageable);
                } else if (banStatus == null) {
                    users = userRepository.findAllByPhoneName(phoneName, pageable);
                } else {
                    users = userRepository.findAllByNotBan(phoneName, pageable);
                }
            }
            List<UserResponse> dtoList = users
                    .stream()
                    .map(user -> {
                                UserResponse response = modelMapper.map(user, UserResponse.class);
                                response.setRoleName(user.getRole().getTitle());
                                List<Address> addressList = addressRepository.findByUserIdAnAndIsDefault(user.getUserId());
                                if (!addressList.isEmpty()) {
                                    response.setDefaultAddress(addressList.get(0).getDescription());
                                }
                                return response;
                            }
                    )
                    .collect(Collectors.toList());
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(users, dtoList);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Booking History Response!", pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> banUser(Integer userId) {
        try {
            User user = findById(userId);
            isPermission(user);
            user.setIsLocked(user.getIsLocked() == null || !user.getIsLocked());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Ban/unban user successful!", null));
        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
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

    private void isPermission(User user) throws UserNotHavePermissionException {
        if (RoleEnum.ADMIN.name().equalsIgnoreCase(user.getRole().getTitle()))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
    }

    private User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Id not found"));
    }
}
