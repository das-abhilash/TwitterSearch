package com.example.abhilash.cleartax_twittersearch.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by sam_chordas on 10/5/15.
 */


@ContentProvider(authority = TweetProvider.AUTHORITY, database = TweetDatabase.class)
public class TweetProvider {
    public static final String AUTHORITY = "com.example.abhilash.cleartax_twittersearch.data";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String Tweets= "tweet";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = TweetDatabase.TWEETS)
    public static class Tweets {
        @ContentUri(
                path = Path.Tweets,
                type = "vnd.android.cursor.dir/tweet"
        )
        public static final Uri CONTENT_URI = buildUri(Path.Tweets);

        @InexactContentUri(
                name = "TWEET_ID",
                path = Path.Tweets + "/*",
                type = "vnd.android.cursor.item/tweet",
                whereColumn = TweetColumns.ID,
                pathSegment = 1
        )
        public static Uri ID(String ID) {
            return buildUri(Path.Tweets, ID);
        }
    }
}

