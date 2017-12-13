package com.gvcgroup.tokengateway.security;

import com.gvcgroup.tokengateway.util.TimeProvider;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
public class TokenHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TokenHelper.class);

    @Value("${app.name}")
    private String APP_NAME;

    @Value("${jwt.secret}")
    public String SECRET;

    @Value("${jwt.header}")
    private String AUTH_HEADER;

    @Autowired
    TimeProvider timeProvider;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String getUsernameFromToken(String token) {
        String username = null;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt = null;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return issueAt;
    }

    public String refreshToken(String token, Date newExpirationDate) {
        String refreshedToken = null;
        Date a = timeProvider.now();
        if(isTokenValid(token)){
            try {
                final Claims claims = this.getAllClaimsFromToken(token);
                claims.setIssuedAt(a);
                refreshedToken = Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(generateExpirationDate(newExpirationDate))
                        .signWith( SIGNATURE_ALGORITHM, SECRET.getBytes("UTF-8") )
                        .compact();
            } catch (SignatureException e) {
                LOG.info(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return refreshedToken;
    }

    public String generateToken(String username, Date expirationDate) {
        try {
            return Jwts.builder()
                    .setIssuer( APP_NAME )
                    .setSubject(username)
                    .setIssuedAt(timeProvider.now())
                    .setExpiration(generateExpirationDate(expirationDate))
                    .signWith( SIGNATURE_ALGORITHM, SECRET.getBytes("UTF-8") )
                    .compact();
        } catch (UnsupportedEncodingException e) {
            LOG.warn(e.getMessage());
        }
        return null;
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            LOG.warn(e.getMessage());
        } catch (ExpiredJwtException ex) {
            LOG.warn(ex.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return claims;
    }

    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET.getBytes("UTF-8"))
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException se ) {
            LOG.warn(se.getMessage());
        } catch (ExpiredJwtException ex) {
            LOG.warn(ex.getMessage());
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public String getAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader(AUTH_HEADER);
    }

    /**
     *
     * @param date are the mills of validity of the token
     * @return date of expiration
     */
    private Date generateExpirationDate(Date date) {
        Date expirationDate = new Date(timeProvider.now().getTime() + date.getTime());
        LOG.info("Expiration date: {}", expirationDate.getTime());
        return expirationDate;
    }

}
