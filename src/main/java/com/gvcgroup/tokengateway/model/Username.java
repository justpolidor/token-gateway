package com.gvcgroup.tokengateway.model;

import java.io.Serializable;

public class Username implements Serializable {

    private String username;

    public Username(String username) {
        this.username = username;
    }

    public Username() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Username{" +
                "username='" + username + '\'' +
                '}';
    }
}
