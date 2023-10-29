package iclean.code.data.mapper;

import iclean.code.data.domain.MoneyRequest;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequestDTO;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class MoneyRequestMapper {
    private final ModelMapper modelMapper;
    public MoneyRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<CreateMoneyRequestRequestDTO, MoneyRequest>() {
            @Override
            protected void configure() {
                map().setBalance(source.getBalance());
                map().setRequestDate(Utils.getDateTimeNow());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
