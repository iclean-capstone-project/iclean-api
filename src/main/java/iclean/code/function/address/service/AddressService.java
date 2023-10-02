package iclean.code.function.address.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.address.CreateAddressRequestDTO;
import iclean.code.data.dto.request.address.GetAddressRequestDTO;
import iclean.code.data.dto.request.address.UpdateAddressRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<ResponseObject> getAddresses();

    ResponseEntity<ResponseObject> getAddress(Integer id);

    ResponseEntity<ResponseObject> createAddress(CreateAddressRequestDTO request);

    ResponseEntity<ResponseObject> updateAddress(Integer id, UpdateAddressRequestDTO request);

    ResponseEntity<ResponseObject> deleteAddress(Integer id);
}
