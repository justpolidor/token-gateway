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
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public String refreshToken(String token, Date newExpirationDate) {
        String refreshedToken;
        Date a = timeProvider.now();
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            claims.setIssuedAt(a);
            refreshedToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(generateExpirationDate(newExpirationDate))
                    .signWith( SIGNATURE_ALGORITHM, SECRET.getBytes("UTF-8") )
                    .compact();
        } catch (Exception e) {
            refreshedToken = null;
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
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public Boolean isTokenValid(String token) {
        //String username = getUsernameFromToken(token);
        Date created = getIssuedAtDateFromToken(token);
        Date expiration;
        try {
            expiration = Jwts.parser()
                    .setSigningKey(SECRET.getBytes("UTF-8"))
                    .parseClaimsJws(token).getBody().getExpiration();
            if(!(timeProvider.now().getTime() < (created.getTime() + expiration.getTime()))) {
                return true;
            } //TODO
        } catch (ExpiredJwtException | UnsupportedEncodingException | SignatureException e) {
            LOG.warn(e.getMessage());
        }
        return false;
    }

    public String getAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader(AUTH_HEADER);
    }

    private Date generateExpirationDate(Date date) {
        return new Date(timeProvider.now().getTime() + date.getTime());
    }

}
