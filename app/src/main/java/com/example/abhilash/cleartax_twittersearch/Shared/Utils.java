package com.example.abhilash.cleartax_twittersearch.Shared;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import com.example.abhilash.cleartax_twittersearch.BuildConfig;
import com.example.abhilash.cleartax_twittersearch.service.TwitterIntentService;

import java.io.UnsupportedEncodingException;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by Abhilash on 5/25/2016.
 */
public class Utils {

    public static String getBase64String(String value) throws UnsupportedEncodingException {
        return Base64.encodeToString(value.getBytes("UTF-8"), Base64.NO_WRAP);
    }

    public static String Auth() {
        Twitter twitter = new TwitterFactory().getInstance();


        AccessToken accessToken = new AccessToken(BuildConfig.Request_Token, BuildConfig.Request_Token_Secret);
        twitter.setOAuthConsumer(BuildConfig.Consumer_Key, BuildConfig.Consumer_Serect_Key);
        twitter.setOAuthAccessToken(accessToken);
        String tweeet = null;
        try {
            Query query = new Query("cleartax_in");
            QueryResult result;

            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                tweeet += ("\n" + "@" + tweet.getUser().getScreenName() + " - " + tweet.getText() + "\n \n");
            }
        } catch (TwitterException te) {
            te.printStackTrace();
        }
        return tweeet;
    }
}
