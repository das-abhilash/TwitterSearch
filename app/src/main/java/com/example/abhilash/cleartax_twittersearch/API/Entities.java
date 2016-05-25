package com.example.abhilash.cleartax_twittersearch.API;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhilash on 5/24/2016.
 */
public class Entities {

    @SerializedName("user_mentions")
    private List<Object> UserMentions = new ArrayList<Object>();

    public List<Object> getUserMentions() {
        return UserMentions;
    }

    public void setUserMentions(List<Object> UserMentions) {
        this.UserMentions = UserMentions;
    }

}
