package com.athletiq.backend.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "athletiq.security.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpirationMinutes = 30;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpirationMinutes() {
        return accessTokenExpirationMinutes;
    }

    public void setAccessTokenExpirationMinutes(long accessTokenExpirationMinutes) {
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    }
}
