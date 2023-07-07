package com.example.jwt.jwt;

import com.example.jwt.model.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour token'ın 24 saat kullanım süresi var.

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getEmail())) // tokenin bu alanında kullanıcı kimlik bilgileri bulunur.
                .setIssuer("EminYildiz") // token'ın kim tarafından oluşturulur onu gösterir.
                .claim("roles",user.getRoles().toString()) // token'a kullanıcı yetkilerini verdik.
                .setIssuedAt(new Date()) // token'ın oluşturulma tarihi
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION)) // token'ın kullanımının biteceği tarih
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // token oluştururken kullanılacak olan algoritma ve, secret key burada belirtilir.
                .compact();
    }

    // JWT doğrulaması için kullanılır.
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed");
        }

        return false;
    }

    // token içerisinde yer alan subjet kısmını döndürür. Bu kısımda kullnıcı kimliği ve e-posta alanları bulunur.
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    // Claims jwt objesi olarak geçer body jwt token içerisinde yer alan bilgileri içerir.
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
