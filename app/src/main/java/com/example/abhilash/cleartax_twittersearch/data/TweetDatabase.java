package com.example.abhilash.cleartax_twittersearch.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sam_chordas on 10/5/15.
 */


@Database(version = TweetDatabase.VERSION)
public class TweetDatabase {
    private TweetDatabase() {
    }

    public static final int VERSION = 1;

    @Table(TweetColumns.class)
    public static final String TWEETS = "TwitterSearch";
}
