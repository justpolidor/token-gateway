package com.gvcgroup.tokengateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTokenState {

    private String accessToken;
    private Long expiresIn;
    private Boolean expired;

    public UserTokenState() {
    }

    public UserTokenState(String accessToken, Long expiresIn, Boolean expired) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.expired = expired;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTokenState that = (UserTokenState) o;

        if (accessToken != null ? !accessToken.equals(that.accessToken) : that.accessToken != null) return false;
        return expiresIn != null ? expiresIn.equals(that.expiresIn) : that.expiresIn == null;
    }

    @Override
    public int hashCode() {
        int result = accessToken != null ? accessToken.hashCode() : 0;
        result = 31 * result + (expiresIn != null ? expiresIn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserTokenState{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
