package iclean.code.function.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import iclean.code.data.domain.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Integer userId) throws JsonProcessingException;
    RefreshToken findByToken(String token) throws JsonProcessingException;
    void deleteByRefreshToken(String userId);
    RefreshToken verifyExpiration(RefreshToken token);
}
