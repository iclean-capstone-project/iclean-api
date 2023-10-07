package iclean.code.function.address.service.impl;

import iclean.code.data.domain.Address;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.address.CreateAddressRequestDTO;
import iclean.code.data.dto.request.address.UpdateAddressRequestDTO;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.address.GetAddressResponseDetailDto;
import iclean.code.data.dto.response.address.GetAddressResponseDto;
import iclean.code.data.repository.AddressRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.address.service.AddressService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAddresses(Integer userId, Pageable pageable, String search) {
        try {
            Page<Address> addresses = addressRepository.findByUserId(userId, Utils.removeAccentMarksForSearching(search), pageable);

            List<GetAddressResponseDto> dtoList = addresses
                    .stream()
                    .map(address -> modelMapper.map(address, GetAddressResponseDto.class))
                    .collect(Collectors.toList());

            PageResponseObject pageResponseObject = Utils.convertToPageResponse(addresses, Collections.singletonList(dtoList));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Addresses List",
                            pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAddress(Integer id, Integer userId) {
        try {
            Address address = findAddress(id);
            if (!Objects.equals(userId, address.getUser().getUserId())) {
                throw new UserNotHavePermissionException();
            }
            GetAddressResponseDetailDto response = modelMapper.map(address, GetAddressResponseDetailDto.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Address Detail",
                            response));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    private Address findAddress(int id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Address ID: %s is not exist", id)));
    }

    private User findUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }

    @Override
    public ResponseEntity<ResponseObject> createAddress(CreateAddressRequestDTO request, Integer userId) {
        try {
            Address address = modelMapper.map(request, Address.class);
            User user = findUser(userId);
            address.setUser(user);
            address.setCreateAt(Utils.getDateTimeNow());
            addressRepository.save(address);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Address Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateAddress(Integer id, UpdateAddressRequestDTO request, Integer userId) {
        try {
            Address address = findAddress(id);
            if (!Objects.equals(address.getUser().getUserId(), userId))
                throw new UserNotHavePermissionException();

            modelMapper.map(request, address);
            address.setUpdateAt(Utils.getDateTimeNow());
            addressRepository.save(address);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Address Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteAddress(Integer id, Integer userId) {
        try {
            Address address = findAddress(id);
            if (!Objects.equals(address.getUser().getUserId(), userId))
                throw new UserNotHavePermissionException();

            addressRepository.delete(address);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Address Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> setDefaultAddress(Integer id, Integer userId) {
        try {
            Address address = findAddress(id);
            if (!Objects.equals(address.getUser().getUserId(), userId))
                throw new UserNotHavePermissionException();

            List<Address> defaultAddresses = addressRepository.findByUserIdAnAndIsDefault(userId);
            for (Address defaultAddress : defaultAddresses) {
                defaultAddress.setIsDefault(false);
                addressRepository.save(defaultAddress);
            }

            address.setIsDefault(true);
            address.setUpdateAt(Utils.getDateTimeNow());
            addressRepository.save(address);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Address Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
}
