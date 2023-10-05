package iclean.code.function.address.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.address.CreateAddressRequestDTO;
import iclean.code.data.dto.request.address.UpdateAddressRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<ResponseObject> getAddresses(Integer userId);

    ResponseEntity<ResponseObject> getAddress(Integer id, Integer userId);

    ResponseEntity<ResponseObject> createAddress(CreateAddressRequestDTO request, Integer userId);

    ResponseEntity<ResponseObject> updateAddress(Integer id, UpdateAddressRequestDTO request, Integer userId);

    ResponseEntity<ResponseObject> deleteAddress(Integer id, Integer userId);

    ResponseEntity<ResponseObject> setDefaultAddress(Integer id, Integer userId);
}
