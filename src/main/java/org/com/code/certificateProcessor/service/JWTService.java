package org.com.code.certificateProcessor.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import org.com.code.certificateProcessor.exeption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Setter
public class JWTService {

    @Value("${app.jwt.secret}")
    private String JWT_SECRET;

    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;

    private static final String KeyUserAuth_Token = "user_%s:auth_%s";
    private static final String KeyToken_UserAuth = "token_%s";
    private static final long jwtExpiryInHours = 24;

    public String getJwtToken(String userId, String auth){

        Map<String, String> claimsOfRandomDigit= new HashMap<>();

        String random = UUID.randomUUID().toString();
        claimsOfRandomDigit.put("random", random);
        String newToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .setClaims(claimsOfRandomDigit)
                .compact();

        String keyUserAuth_Token = String.format(KeyUserAuth_Token, userId,auth);
        objectRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations ops) throws DataAccessException {
                ops.multi();

                String oldToken = (String) ops.opsForValue().get(keyUserAuth_Token);
                String keyToken_UserAuth = String.format(KeyToken_UserAuth, oldToken);
                ops.delete(keyToken_UserAuth);
                ops.opsForValue().set(keyUserAuth_Token, newToken,Duration.ofHours(jwtExpiryInHours));
                keyToken_UserAuth = String.format(KeyToken_UserAuth, newToken);
                ops.opsForValue().set(keyToken_UserAuth, userId+"_"+ auth,Duration.ofHours(jwtExpiryInHours));

                return ops.exec();
            }
        });

        return newToken;
    }

    public String checkToken(String authToken) {
        if(authToken==null||authToken.trim().isEmpty())
            throw new ResourceNotFoundException("未携带token");

        return (String) objectRedisTemplate.opsForValue().get(String.format(KeyToken_UserAuth, authToken));
    }

    public void deleteToken(String token) {
        objectRedisTemplate.opsForHash().delete("Auth", token);
    }
}
