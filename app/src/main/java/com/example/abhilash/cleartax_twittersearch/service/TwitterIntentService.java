package com.example.abhilash.cleartax_twittersearch.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Abhilash on 5/25/2016.
 */
public class TwitterIntentService extends IntentService {
public TwitterIntentService (){
    super(TwitterTaskService.class.getName());
}
    public TwitterIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TwitterTaskService twitterTaskService = new TwitterTaskService(this);
        Bundle args = new Bundle();
        args.putString("SerachQuery", intent.getStringExtra("SerachQuery"));
        twitterTaskService.onRunTask(new TaskParams("SerachQuery", args));
    }
}
