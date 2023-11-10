package iclean.code.data.mapper;

import iclean.code.data.domain.Transaction;
import iclean.code.data.dto.response.transaction.GetTransactionDetailResponse;
import iclean.code.data.dto.response.transaction.GetTransactionResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    private final ModelMapper modelMapper;
    public TransactionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<Transaction, GetTransactionResponse>() {
            @Override
            protected void configure() {
                map().setTransactionType(String.valueOf(source.getTransactionTypeEnum()));
                map().setTransactionStatus(String.valueOf(source.getTransactionStatusEnum()));
                map().setAmount(source.getAmount());
            }
        });

        modelMapper.addMappings(new PropertyMap<Transaction, GetTransactionDetailResponse>() {
            @Override
            protected void configure() {
                map().setTransactionStatus(String.valueOf(source.getTransactionStatusEnum()));
                map().setAmount(source.getAmount());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
