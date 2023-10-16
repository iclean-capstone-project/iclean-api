package iclean.code.function.imgtype.service.impl;

import iclean.code.data.domain.ImgType;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgtype.AddImgTypeRequest;
import iclean.code.data.dto.request.imgtype.UpdateImgTypeRequest;
import iclean.code.data.dto.response.imgtype.GetImgTypeDTO;
import iclean.code.data.repository.ImgTypeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.imgtype.service.ImgTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImgTypeServiceImpl implements ImgTypeService {

    @Autowired
    private ImgTypeRepository imgTypeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllImgType() {
        List<ImgType> imgTypes = imgTypeRepository.findAll();
        GetImgTypeDTO imgTypesResponse = modelMapper.map(imgTypes, GetImgTypeDTO.class);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "All Image type", imgTypesResponse));
    }

    @Override
    public ResponseEntity<ResponseObject> getImgTypeById(int imgTypeId) {
        try {
            ImgType imgType =findImgType(imgTypeId);
            GetImgTypeDTO imgTypesResponse = modelMapper.map(imgType, GetImgTypeDTO.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(), "Image type", imgTypesResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addImgType(AddImgTypeRequest imgTypeRequest) {
        try {
            ImgType imgType = modelMapper.map(imgTypeRequest, ImgType.class);
            imgType.setTitleImgType(imgTypeRequest.getTitleImgType());

            imgTypeRepository.save(imgType);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Image type Successfully!", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateImgType(int imgTypeId, UpdateImgTypeRequest imgTypeRequest) {
        try {
            ImgType imgTypeForUpdate = findImgType(imgTypeId);
            ImgType imgType = modelMapper.map(imgTypeForUpdate, ImgType.class);

            imgType.setTitleImgType(imgTypeRequest.getTitleImgType());
            imgTypeRepository.save(imgType);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Image type Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteImgType(int imgTypeId) {
        try {
            ImgType imgTypeForDelete = findImgType(imgTypeId);
            imgTypeRepository.delete(imgTypeForDelete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete Image type Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    private ImgType findImgType(int imgTypeId) {
        return imgTypeRepository.findById(imgTypeId)
                .orElseThrow(() -> new NotFoundException("Image type is not exist"));
    }
}
