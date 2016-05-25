package com.example.abhilash.cleartax_twittersearch.API;

/**
 * Created by Abhilash on 5/23/2016.
 */


import com.google.gson.annotations.SerializedName;

public class Tweet {


    @SerializedName("statuses")
    private Statuses statuses;

    @SerializedName("search_metadata")
    private Metadata metadata;


    public Statuses getStatuses() {
        return statuses;
    }

    public void setStatuses(Statuses statuses) {
        this.statuses = statuses;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
    