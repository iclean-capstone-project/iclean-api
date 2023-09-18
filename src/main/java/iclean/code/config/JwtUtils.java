package iclean.code.config;

import iclean.code.data.dto.response.UserPrinciple;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtils {

    private final String jwtSecret = "wyk52dSUWe6vaMYOsdOrBlWwE0kyQjj6zzPMy4xWVzbRLzPqT31AQaYqpZy3q4w8RR6of0LKPHrr+wJc7NxelA==";
    public String createAccessToken(UserPrinciple userPrinciple) {

        return Jwts.builder().setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 6000000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String createRefreshToken(UserPrinciple userPrinciple) {

        return Jwts.builder().setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 6000000 * 7))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getJwt(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer")){
            return authHeader.replace("Bearer", "");
        }
        return null;
    }
    public boolean validateToken(String token) throws SignatureException, MalformedJwtException, UnsupportedJwtException, ExpiredJwtException, IllegalArgumentException{
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch (SignatureException | ExpiredJwtException | MalformedJwtException | UnsupportedJwtException |
                IllegalArgumentException e){
        }
        return false;
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
