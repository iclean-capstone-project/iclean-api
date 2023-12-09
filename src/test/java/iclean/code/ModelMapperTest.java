package iclean.code;

import iclean.code.data.domain.User;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ModelMapperTest {
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testMapping() {
        // Create source and target objects
    }
}
