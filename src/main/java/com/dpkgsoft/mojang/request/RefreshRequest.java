package com.dpkgsoft.mojang.request;

import com.dpkgsoft.mojang.Profile;

public class RefreshRequest {
    private final String accessToken;
    private final String clientToken;
    private final Profile profile;

    public RefreshRequest(String accessToken, String clientToken, Profile profile) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.profile = profile;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public Profile getProfile() {
        return profile;
    }
}
