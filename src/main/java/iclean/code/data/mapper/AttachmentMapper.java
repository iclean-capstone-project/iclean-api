package iclean.code.data.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {
    private final ModelMapper modelMapper;

    public AttachmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
