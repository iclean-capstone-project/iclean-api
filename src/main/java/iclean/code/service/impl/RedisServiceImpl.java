package iclean.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.data.domain.RefreshToken;
import iclean.code.service.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void setValueRefreshToken(String key, RefreshToken value) throws JsonProcessingException {
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
    }

    @Override
    public RefreshToken getValueRefreshToken(String key) throws JsonProcessingException {
        String jsonString = (String) redisTemplate.opsForValue().get(key);
        return objectMapper.readValue(jsonString, RefreshToken.class);
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}
