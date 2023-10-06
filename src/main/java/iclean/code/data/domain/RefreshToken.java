package iclean.code.data.domain;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshToken {

    private Integer userId;

    private String token;

    private Instant expiryDate;

}
