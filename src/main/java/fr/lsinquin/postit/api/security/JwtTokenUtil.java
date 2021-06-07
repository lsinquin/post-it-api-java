package fr.lsinquin.postit.api.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Util class for JWT handling concerns : generation, validation, decoding
 */
@Component
@NoArgsConstructor
public class JwtTokenUtil {

    /**
     * Secret key used for the signature algorithm. The util class is based on the HS256 signature algorithm.
     * The key is generated at the start the application. It means that all tokens generated before a restart are not valid.
     */
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generates a new JWT token for a specific user
     * @param subject The user represented by his mail address
     * @return String representing the generated JWT
     */
    public String generateAccessToken(String subject) {
        Date expirationDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 1 Day

        return Jwts.builder()
                .setIssuer("post-it.com")
                .setSubject(subject)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * Retrieves the user the token was generated for.
     * Usually the JWT was validated before calling this method with the {@link #validate(String) appropriate method}.
     * @param jwt String representing a JWT
     * @return The user represented by his mail address
     */
    public String getUserSubject(String jwt) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody().getSubject();
    }

    /**
     * Verify that a String is a valid JWT
     * @param jws The String to be validated
     * @return true if it's a valid JWT. False otherwise
     */
    public boolean validate(String jws) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws);

            return true;
        } catch (JwtException e) {
            System.out.println(e.getMessage());

            return false;
        }
    }

}
