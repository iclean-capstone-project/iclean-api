package iclean.code.data.mapper;

import iclean.code.data.domain.Transaction;
import iclean.code.data.dto.response.transaction.GetTransactionDetailResponseDto;
import iclean.code.data.dto.response.transaction.GetTransactionResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    private final ModelMapper modelMapper;
    public TransactionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<Transaction, GetTransactionResponseDto>() {
            @Override
            protected void configure() {
                map().setTransactionStatus(String.valueOf(source.getTransactionStatusEnum()));
                map().setBalance(source.getAmount());
            }
        });

        modelMapper.addMappings(new PropertyMap<Transaction, GetTransactionDetailResponseDto>() {
            @Override
            protected void configure() {
                map().setTransactionStatus(String.valueOf(source.getTransactionStatusEnum()));
                map().setBalance(source.getAmount());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
