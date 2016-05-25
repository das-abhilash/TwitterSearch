package com.example.abhilash.cleartax_twittersearch.ui;

import android.os.AsyncTask;

import com.example.abhilash.cleartax_twittersearch.Shared.Utils;

/**
 * Created by Abhilash on 5/25/2016.
 */
class DownloadTwitterTask extends AsyncTask<Void, Void, String> {
    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=";

    private MainActivity mainActivity;

    public DownloadTwitterTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Void... searchTerms) {
        String tweeet = Utils.Auth();
        return tweeet;
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.UpdateUI(s);
    }
}
