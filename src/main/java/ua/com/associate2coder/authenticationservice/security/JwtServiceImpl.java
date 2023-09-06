package ua.com.associate2coder.authenticationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService, Serializable {


    @Value(value = "${jwt-issuer}")
    private String JWT_ISSUER;
    @Value(value = "${jwt-validity-in-milliseconds}")
    private long JWT_VALIDITY_DURATION;
    @Value(value = "${jwt-signing-key}")
    private String JWT_SIGNING_KEY;


    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, new HashMap<>());
    }

    @Override
    public String generateToken
            (
                    UserDetails userDetails,
                    Map<String, Object> extraClaims
                    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY_DURATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String tokenUsername = getUsernameFromToken(token);
        return !isTokenExpired(token)
                && tokenUsername.equals(userDetails.getUsername());
    }

    private Claims getAllClaimsFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SIGNING_KEY));
    }


}