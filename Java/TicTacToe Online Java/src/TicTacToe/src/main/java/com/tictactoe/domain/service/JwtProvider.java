package com.tictactoe.domain.service;

import com.tictactoe.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Getter
public class JwtProvider {
private final SecretKey secretKey;
private final long accessTokenTime;
private final long refreshTokenTime;

public JwtProvider(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration}") long accessTime,
                   @Value("${jwt.refresh-token-expiration}") long refreshTime) {

    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            Base64.getEncoder().encodeToString(secret.getBytes())));
    this.accessTokenTime = accessTime;
    this.refreshTokenTime = refreshTime;
    }

public String generateAccessToken(User user) {
Date now = new Date();
JwtBuilder jwtBuilder = Jwts.builder();
//установление субъекта по ID пользователя (на кого выдается токен)
jwtBuilder.subject(user.getId().toString());
//пользовательское утверждение(claim) с именем "roles". Хранит List с ролями пользователя
jwtBuilder.claim("roles", user.getRoles());
//время создания токена
jwtBuilder.issuedAt(now);
//время истчения врока действия токена
jwtBuilder.expiration(new Date(now.getTime() + accessTokenTime));
//подпись токена через ключ secretKey
jwtBuilder.signWith(secretKey);
return jwtBuilder.compact();
}

public String generateRefreshToken(User user) {
Date now = new Date();
JwtBuilder jwtBuilder = Jwts.builder();
jwtBuilder.subject(user.getId().toString());
jwtBuilder.issuedAt(now);
jwtBuilder.expiration(new Date(now.getTime() + refreshTokenTime));
jwtBuilder.signWith(secretKey);
return jwtBuilder.compact();
}

private boolean validateToken(String token) {
try {
    Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token);
    return true;
} catch (JwtException | IllegalArgumentException e) {
    return false;
    }
}

public boolean validateAccessToken(String token) {
return validateToken(token);
}

public boolean validateRefreshToken(String token) {
return validateToken(token);
}

/*Claims - это типизированная сущность токена для служебной информации,
хранящаяся в токене в виде мапы(Map<String, Object> claims). Есть набор
дефолтных(стандартных) Claims, например:
subject,  issuedAt, expiration - это ключи(записываются в сокращенном виде)
Соответственно в value пишутся передаваемые объекты(тип зависит от конкретного claim).
Для примера: .subject() == .claim("sub", user.getId().toString()) - одно и то же.
А есть самописные(пользовательские) Claims, в нашем случае <"roles", List<Role>roles>.*/
public Claims getClaims(String token) {
return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
}

}
