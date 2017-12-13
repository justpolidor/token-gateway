package com.gvcgroup.tokengateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTokenState {

    private String token;
    private Long expiration;
    private Boolean isValid;

    public UserTokenState() {
    }

    public UserTokenState(String token, Long expiration, Boolean isValid) {
        this.token = token;
        this.expiration = expiration;
        this.isValid = isValid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTokenState that = (UserTokenState) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        return expiration != null ? expiration.equals(that.expiration) : that.expiration == null;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserTokenState{" +
                "token='" + token + '\'' +
                ", expiration=" + expiration +
                '}';
    }
}
