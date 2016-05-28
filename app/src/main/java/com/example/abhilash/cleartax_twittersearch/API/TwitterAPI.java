
package com.example.abhilash.cleartax_twittersearch.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Abhilash on 5/23/2016.
 */

public interface TwitterAPI {

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<TwitterTokenType> getToken(
            @Field("grant_type") String grantType

    );

    @GET("/1.1/search/tweets.json")
    Call<Tweet> getTweets(
            @Query("q") String SearchTerm
           ,@Query("count") String count);
    }

