/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.security;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * A service to work with JWT tokens
 */
@Service
@ConfigurationProperties(prefix = "jwt")
@ConfigurationPropertiesScan("org.eclipse.hawkbit.security")
public class JwtTokenService implements TokenService {

    /** Token expiration in seconds */
    private int expirationSec = 86400;
    
    /** Token secret key to calculate hash */
    private String secret; // min 32 chars

    /** Created secret key out of the secret */
    private SecretKey secretKey;

    public JwtTokenService() {
        super();
        setSecret("abcdefghijklmnopqrstuvwxyz1234567890");
    }

    public int getExpirationSec() {
        return this.expirationSec;
    }

    public void setExpirationSec(int expirationSec) {
        this.expirationSec = expirationSec;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    /**
     * Generate a new JWT token with the given username as subject and
     * tenant in a custom field.
     */
    @Override
    public String generate(final String tenant, final String username) {
        final ZonedDateTime expiration = ZonedDateTime.now().plusSeconds(expirationSec);

        return Jwts.builder()
                .setSubject(username)
                .claim("tenant", tenant)
                .setExpiration(Date.from(expiration.toInstant()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Verify the token and return information stored in it
     */
    @Override
    public Map<String, String> verify(final String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(token);
        return parseClaims(jws.getBody());
    }

    /**
     * Convert information from the JWT token body into a string map
     * @param claims extracted token body
     * @return map of strings conatining keys "tenant", "username", "expiration"
     */
    private static Map<String, String> parseClaims(final Claims claims) {
        Map<String, String> result = new HashMap<>();
        result.put("tenant", claims.get("tenant").toString());
        result.put("username", claims.getSubject());
        result.put("expiration", claims.getExpiration().toString());

        return result;
    }

}