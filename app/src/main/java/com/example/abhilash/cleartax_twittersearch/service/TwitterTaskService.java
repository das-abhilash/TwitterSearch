package com.example.abhilash.cleartax_twittersearch.service;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;


import com.example.abhilash.cleartax_twittersearch.API.Statuses;
import com.example.abhilash.cleartax_twittersearch.API.Tweet;
import com.example.abhilash.cleartax_twittersearch.API.TwitterAPI;
import com.example.abhilash.cleartax_twittersearch.API.TwitterTokenType;
import com.example.abhilash.cleartax_twittersearch.R;
import com.example.abhilash.cleartax_twittersearch.Shared.Constants;
import com.example.abhilash.cleartax_twittersearch.Shared.Utils;
import com.example.abhilash.cleartax_twittersearch.data.TweetColumns;
import com.example.abhilash.cleartax_twittersearch.data.TweetProvider;
import com.example.abhilash.cleartax_twittersearch.ui.MainActivity;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Abhilash on 5/24/2016.
 */

public class TwitterTaskService extends GcmTaskService {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TWEET_STATUS_OK, TWEET_STATUS_INVALID_DATA, TWEET_STATUS_AUTHO_PROBLEM, TWEET_STATUS_FAILED_CONNECT_SERVER})
    public @interface TweetStatus {
    }

    public static final int TWEET_STATUS_OK = 0;
    public static final int TWEET_STATUS_INVALID_DATA = 1;
    public static final int TWEET_STATUS_AUTHO_PROBLEM = 3;
    public static final int TWEET_STATUS_FAILED_CONNECT_SERVER = 4;


    Statuses statuses;
    private Context mContext;
    private TwitterAPI service;
    private TwitterAPI service1;
    String SerachQuery;

    public TwitterTaskService() {
        super();
    }

    public TwitterTaskService(Context context) {
        mContext = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public int onRunTask(TaskParams params) {
        if (mContext == null) {
            mContext = this;
        }
        SerachQuery = params.getExtras().getString("SerachQuery");
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Basic " + Utils.getBase64String(Constants.BEARER_TOKEN_CREDENTIALS))
                        .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(Constants.TWITTER_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = restAdapter.create(TwitterAPI.class);
        Call<TwitterTokenType> call = service.getToken("client_credentials");
        call.enqueue(new Callback<TwitterTokenType>() {

            @Override
            public void onResponse(Call<TwitterTokenType> call, retrofit2.Response<TwitterTokenType> response) {

                String AcessToken = response.body().getAccessToken();

                getData(AcessToken);
                setTwitterStatus(mContext, TWEET_STATUS_OK);
            }

            @Override
            public void onFailure(Call<TwitterTokenType> call, Throwable t) {
                setTwitterStatus(mContext, TWEET_STATUS_FAILED_CONNECT_SERVER);
            }

        });
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void getData(final String AcessToken) {
        Interceptor interceptor1 = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + AcessToken)
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        };

// Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder1 = new OkHttpClient.Builder();
        builder1.interceptors().add(interceptor1);
        OkHttpClient client1 = builder1.build();

        Retrofit restAdapter1 = new Retrofit.Builder()
                .baseUrl(Constants.TWITTER_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client1)
                .build();

        service1 = restAdapter1.create(TwitterAPI.class);
        Call<Tweet> call1 = service1.getTweets(SerachQuery, "100");
        call1.enqueue(new retrofit2.Callback<Tweet>() {


            @Override
            public void onResponse(Call<Tweet> call, retrofit2.Response<Tweet> response) {
                statuses = response.body().getStatuses();

                int size = statuses.size();
                if (size == 0) {
                    setTwitterStatus(mContext, TWEET_STATUS_INVALID_DATA);
                }
                ArrayList<ContentProviderOperation> tweet = new ArrayList<ContentProviderOperation>();
                tweet.add(ContentProviderOperation.newDelete(TweetProvider.Tweets.CONTENT_URI).build());
                for (int i = 0; i < size; i++) {
                    ContentValues values = new ContentValues();

                    values.put(TweetColumns.ID, String.valueOf(statuses.get(i).getId()));
                    values.put(TweetColumns.ID_STR, String.valueOf(statuses.get(i).getIdStr()));
                    values.put(TweetColumns.TEXT, statuses.get(i).getText());
                    values.put(TweetColumns.SCREEN_NAME, "@" + statuses.get(i).getUser().getScreenName());
                    values.put(TweetColumns.NAME, statuses.get(i).getUser().getName());

                    values.put(TweetColumns.PROFILE_IMAGE, statuses.get(i).getUser().getProfileImageUrl());
                    values.put(TweetColumns.USER_MENTIONS, statuses.get(i).getEntities().getUserMentions().size());

                    values.put(TweetColumns.CREATED_AT, statuses.get(i).getDateCreated());
                    tweet.add(ContentProviderOperation.newInsert(TweetProvider.Tweets.CONTENT_URI).withValues(values).build());
                }
                try {
                    mContext.getContentResolver().
                            applyBatch(TweetProvider.AUTHORITY, tweet);
                    MainActivity.SpinnerVisibility(false);
                } catch (RemoteException | OperationApplicationException e) {

                    e.printStackTrace();

                }


                setTwitterStatus(mContext, TWEET_STATUS_OK);
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                setTwitterStatus(mContext, TWEET_STATUS_AUTHO_PROBLEM);

            }
        });


    }

    static private void setTwitterStatus(Context c, @TweetStatus int TweetStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_tweet_status_key), TweetStatus);
        spe.commit();
    }

}



