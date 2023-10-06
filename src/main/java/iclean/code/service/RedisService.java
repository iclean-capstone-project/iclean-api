package iclean.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import iclean.code.data.domain.RefreshToken;

public interface RedisService {
    void setValueRefreshToken(String key, RefreshToken value) throws JsonProcessingException;
    RefreshToken getValueRefreshToken(String key) throws JsonProcessingException;
    void deleteKey(String key);
}
