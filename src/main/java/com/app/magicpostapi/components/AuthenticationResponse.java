package com.app.magicpostapi.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponse {

    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("refreshToken")
    private String refreshToken;
    @JsonProperty("role")
    private String role;
    @JsonProperty("idBranch")
    private String idBranch;

    public AuthenticationResponse(String accessToken, String refreshToken, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public AuthenticationResponse(String accessToken, String refreshToken, String role, String idBranch) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.idBranch = idBranch;
    }
}
