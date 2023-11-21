package iclean.code.data.mapper;

import iclean.code.data.domain.User;
import iclean.code.data.dto.response.feedback.GetDetailHelperResponse;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestUserResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<User, GetMoneyRequestUserResponse>() {
            @Override
            protected void configure() {
                map().setRoleName(source.getRole().getTitle());
            }
        });

        modelMapper.addMappings(new PropertyMap<User, GetDetailHelperResponse>() {
            @Override
            protected void configure() {
                map().setHelperName(source.getFullName());
                map().setHelperAvatar(source.getAvatar());
                map().setPhoneNumber(source.getPhoneNumber());
                map().setHelperId(source.getUserId());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
