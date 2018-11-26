package server.spring.security.jwt;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import shared.dto.UserDTO;

import java.util.Date;
import java.util.HashMap;

/**
 * find here any jwtoken stuff
 */
@Component
public class JwtUtil {


    private String salt = "3dn92d8mn293dm329d8m29d832dm9328dm";

    private Long expiration = 999999L;


//--------------utils--------------------

    /**
     * get all token claims
     * @param token token to read claims from
     * @return claims
     */
    private Claims getClaimsFromToken(String token) {
        if(token == null || token.length()<2)
            return null;
        return Jwts.parser().setSigningKey(salt).parseClaimsJws(token).getBody();
    }

    /**
     *
     * @return current system time
     */
    private Date genCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     *
     * @return current expiration timy in millis
     */
    private Date genExpirationDate() {
        return new Date(System.currentTimeMillis() + this.expiration * 1000);
    }


    /**
     * validate token
     * @param token token to validate
     * @param userDetails to validate with token
     * @return is this token for this user and isn't it expired
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        UserDTO user = (UserDTO) userDetails;
        final String username = this.getUsernameFromToken(token);
        final Date created = this.getCreatedDateFromToken(token);
        return (username.equals(user.getUserName()) && !(this.isTokenExpired(token)));
    }
    //------------create and update token----------------

    /**
     * generates token from usertails
     * @param userDetails
     * @return token
     */
    public String genToken(UserDetails userDetails) {
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", genCurrentDate());
        claims.put("role", userDetails.getAuthorities());
        return this.genToken(claims);
    }

    /**
     * generates token from claims
     * @param claims
     * @return token
     */
    private String genToken(HashMap<String, Object> claims) {
        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(genExpirationDate())
            .signWith(SignatureAlgorithm.HS256, salt)
            .compact();
    }



    //--------------get from token---------------

    /**
     * get creation date from token
     * @param token token to get creation time from
     * @return token's creation date
     */
    public Date getCreatedDateFromToken(String token) {
            final Claims claims = this.getClaimsFromToken(token);
            return new Date((Long) claims.get("created"));
    }

    /**
     * get expiry date from token
     * @param token token to get expiry time from
     * @return
     */
    private Date getExpirationDateFromToken(String token) {
            final Claims claims = this.getClaimsFromToken(token);
            return claims.getExpiration();
    }

    /**
     * checks if token is expired
     * @param token token to check expired or not
     * @return boolean if token is expired or not
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(this.genCurrentDate());
    }

    /**
     * retrieve username from Token
     * @param token to retrieve username from
     * @return username
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
}
