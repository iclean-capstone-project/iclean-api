package iclean.code.data.mapper;

import iclean.code.data.domain.User;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestUserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new LocalDateToStringConverter());
        modelMapper.addMappings(new PropertyMap<User, GetMoneyRequestUserDto>() {
            @Override
            protected void configure() {
                map().setRoleName(source.getRole().getTitle());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
