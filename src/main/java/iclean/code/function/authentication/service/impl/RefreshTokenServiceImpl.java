package iclean.code.function.authentication.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import iclean.code.data.domain.RefreshToken;
import iclean.code.exception.TokenRefreshException;
import iclean.code.function.authentication.service.RefreshTokenService;
import iclean.code.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${iclean.app.jwt-refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RedisService redisService;

    public RefreshToken findByToken(String token) throws JsonProcessingException {
        return redisService.getValueRefreshToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Integer userId) throws JsonProcessingException {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(token);

        redisService.setValueRefreshToken(token, refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            redisService.deleteKey(token.getToken());
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new sign in request");
        }

        return token;
    }

    @Transactional
    @Override
    public int deleteByUserId(Integer userId) {
        return 1;
    }
}
