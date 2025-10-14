package example.oauth2_sample.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
  // token 제공자
  private final String secretKey;
  private final int expiration;
  private Key SECRET_KEY;

  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") int expiration) {
    this.secretKey = secretKey;
    this.expiration = expiration;
    // sha 512 algorithm
    this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
  }

  public String createToken(String email, String role) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("role", role);
    Date now =  new Date();
    String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now) // 발생 시간
            .setExpiration(new Date(now.getTime()) + expiration*60*1000L) // second * ms (minute unit)
            .signWith(SECRET_KEY)
            .compact();
    return token;
  }
}
