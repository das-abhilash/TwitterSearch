package com.example.abhilash.cleartax_twittersearch.API;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abhilash on 5/23/2016.
 */

public class TwitterTokenType {
    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("access_token")
    private String accessToken;

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
