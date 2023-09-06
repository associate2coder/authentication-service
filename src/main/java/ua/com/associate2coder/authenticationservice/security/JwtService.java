package ua.com.associate2coder.authenticationservice.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {


    String getUsernameFromToken(String token);

    <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver);

    Date getExpirationDateFromToken(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails,Map<String, Object> extraClaims);

    Boolean isTokenValid(String token, UserDetails userDetails);

}
