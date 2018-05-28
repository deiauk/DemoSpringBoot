package com.example.lalala.demo.security;

public class UserTokenState {
    private String accessToken;
    private Long expires_in;
    private Long userId;

    public UserTokenState() {
        this.accessToken = null;
        this.expires_in = null;
        this.userId = null;
    }

    public UserTokenState(String accessToken, long expires_in, long userId) {
        this.accessToken = accessToken;
        this.expires_in = expires_in;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public Long getUserId() {
        return userId;
    }
}
