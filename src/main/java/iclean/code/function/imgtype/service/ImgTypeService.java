package iclean.code.function.imgtype.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgType.AddImgTypeRequest;
import iclean.code.data.dto.request.imgType.UpdateImgTypeRequest;
import org.springframework.http.ResponseEntity;

public interface ImgTypeService {
    ResponseEntity<ResponseObject> getAllImgType();

    ResponseEntity<ResponseObject> getImgTypeById(int imgTypeId);

    ResponseEntity<ResponseObject> addImgType(AddImgTypeRequest imgTypeRequest);

    ResponseEntity<ResponseObject> updateImgType(int imgTypeId, UpdateImgTypeRequest imgTypeRequest);

    ResponseEntity<ResponseObject> deleteImgType(int imgTypeId);
}
