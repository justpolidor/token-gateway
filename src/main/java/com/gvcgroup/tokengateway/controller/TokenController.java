package com.gvcgroup.tokengateway.controller;


import com.gvcgroup.tokengateway.model.Token;
import com.gvcgroup.tokengateway.model.UserTokenState;
import com.gvcgroup.tokengateway.model.Username;
import com.gvcgroup.tokengateway.security.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {
    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private TokenHelper tokenHelper;

    @Value("${jwt.expiration}")
    private Long expiration;

    @PostMapping(value = "/generate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Username username,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        LOG.info("generate endpoint with username: {}", username);
        String token = tokenHelper.generateToken(username.getUsername(), new Date(expiration));
        response.addHeader("ws-token", token);
        LOG.info("a new token has been generated for user:{} - Token: {}", username, token);
        return ResponseEntity.ok(new UserTokenState(token, expiration, true));
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestBody Token token,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        LOG.info("refresh endpoint with token: {}", token);
        String renewedToken = tokenHelper.refreshToken(token.getToken(),Date.from(Instant.ofEpochMilli(expiration)));
        response.addHeader("ws-token", token.getToken());
        if(renewedToken != null) {
            LOG.info("a new token has been refreshed for user:{} - new token is {}", tokenHelper.getUsernameFromToken(renewedToken),
                    renewedToken);
            return ResponseEntity.ok(new UserTokenState(renewedToken, expiration, true));
        }
        return ResponseEntity.ok(new UserTokenState(token.getToken(),expiration,false));
    }

    @PostMapping(value = "/check")
    public ResponseEntity<?> checkAuthenticationToken(@RequestBody Token token,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        LOG.info("check endpoint with token: {}", token);
        Boolean isValid = tokenHelper.isTokenValid(token.getToken());
        return ResponseEntity.ok(new UserTokenState(token.getToken(),expiration,isValid));
    }


}
