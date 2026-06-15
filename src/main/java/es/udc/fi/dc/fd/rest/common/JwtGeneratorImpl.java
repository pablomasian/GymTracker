package es.udc.fi.dc.fd.rest.common;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtGeneratorImpl implements JwtGenerator {
    // Implementación de generación y validación de JWT (HS512)

    /** Clave de firma. */
    @Value("${project.jwt.signKey}")
    private String signKey;

    /** Minutos de expiración. */
    @Value("${project.jwt.expirationMinutes}")
    private long expirationMinutes;

    /**
     * Genera un token.
     *
     * @param info información del usuario/rol
     * @return el token generado
     */
    @Override
    public String generate(JwtInfo info) {

        Claims claims = Jwts.claims();

    claims.setSubject(info.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000));
        claims.put("userId", info.getUserId());
        claims.put("role", info.getRole());
        
        return Jwts.builder().setClaims(claims).signWith(Keys.hmacShaKeyFor(signKey.getBytes()), SignatureAlgorithm.HS512).compact();

    }

    /**
     * Obtiene la información desde el token.
     *
     * @param token el token JWT
     * @return la información decodificada
     */
    @Override
    public JwtInfo getInfo(String token) {
        
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(signKey.getBytes())).build().parseClaimsJws(token).getBody();

        return new JwtInfo(((Integer) claims.get("userId")).longValue(), claims.getSubject(),
                (String) claims.get("role"));

    }

}
