package com.example.abhilash.cleartax_twittersearch.Shared;

import com.example.abhilash.cleartax_twittersearch.BuildConfig;

/**
 * Created by Abhilash on 5/23/2016.
 */
public class Constants {
    public static final String CONSUMER_KEY = BuildConfig.Consumer_Key;
    public static final String CONSUMER_SECRET = BuildConfig.Consumer_Serect_Key;
    public static final String BEARER_TOKEN_CREDENTIALS = CONSUMER_KEY + ":" + CONSUMER_SECRET;
    public static final String TWITTER_API_URL = "https://api.twitter.com";

}
