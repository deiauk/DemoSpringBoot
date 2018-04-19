package com.example.lalala.demo.security;

public class UserTokenState {
    private String accessToken;
    private Long expires_in;

    public UserTokenState() {
        this.accessToken = null;
        this.expires_in = null;
    }

    public UserTokenState(String accessToken, long expires_in) {
        this.accessToken = accessToken;
        this.expires_in = expires_in;
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
}
