package com.example.abhilash.cleartax_twittersearch.ui;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;

import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.abhilash.cleartax_twittersearch.R;
import com.example.abhilash.cleartax_twittersearch.data.TweetColumns;
import com.example.abhilash.cleartax_twittersearch.data.TweetProvider;
import com.example.abhilash.cleartax_twittersearch.service.TwitterAdapter;
import com.example.abhilash.cleartax_twittersearch.service.TwitterIntentService;
import com.example.abhilash.cleartax_twittersearch.service.TwitterTaskService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String SEARCH_QUERY = "ipl";

    static ProgressBar spinner;
    TextView twitterData;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private Intent mServiceIntent;
    private static final int CURSOR_LOADER_ID = 0;
    long period = 60000L;
    long flex = 10L;
    String periodicTag = "periodic";
    boolean connected;
    View emptyView;
    TwitterAdapter twitterAdapter;
    Cursor cursor;
    boolean filtered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        emptyView = findViewById(R.id.recycler_view_empty);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        swipeRefreshLayout.setRefreshing(false);
                                                        UpdateData();
                                                    }
                                                }
        );


        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);


        UpdateData();

        if (connected) {
            Bundle bundle = new Bundle();
            bundle.putString("SerachQuery", SEARCH_QUERY);
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(TwitterTaskService.class)
                    .setPeriod(period)
                            //.setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .setExtras(bundle)
                    .build();

            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filtered = !filtered;
                    if (filtered) {
                        Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clockwise_rotation);
                        fab.startAnimation(rotation);

                        Filter(filtered);
                    } else {
                        Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anticlockwise_rotation);
                        fab.startAnimation(rotation);
                        Filter(filtered);
                    }
                }
            });
        }


        //Uncomment the <code>DTT.execute();</code> to fetch data through twitter4j library
        DownloadTwitterTask DTT = new DownloadTwitterTask(this);
        // DTT.execute();

    }


    private void showAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);


        alertDialog.setTitle("No Internet connection.");

        alertDialog.setMessage("You have no internet connection. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "This is OLD Data. Connect to the Internet to Refresh it", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public static void SpinnerVisibility(boolean visibility) {
        if (visibility)
            spinner.setVisibility(View.VISIBLE);
        else spinner.setVisibility(View.GONE);
    }

    public void Filter(boolean filter) {
        String sortOrder = null;
        if (filter)
            sortOrder = TweetColumns.USER_MENTIONS + " DESC LIMIT 3";
        Cursor data = getApplicationContext().getContentResolver().query(TweetProvider.Tweets.CONTENT_URI,
                null, null, null, sortOrder);
        twitterAdapter.swapCursor(data);
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void UpdateData() {
        connected = isNetworkAvailable();
        if (connected) {
            SpinnerVisibility(true);

            mServiceIntent = new Intent(this, TwitterIntentService.class);
            mServiceIntent.putExtra("SerachQuery", SEARCH_QUERY);
            startService(mServiceIntent);
        } else {
            showAlert();
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        if (filtered)
            sortOrder = TweetColumns.USER_MENTIONS + " DESC LIMIT 3";
        return new CursorLoader(this, TweetProvider.Tweets.CONTENT_URI,
                null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (filtered) {
            String sortOrder = TweetColumns.USER_MENTIONS + " DESC LIMIT 3";
            Cursor c = getApplicationContext().getContentResolver().query(TweetProvider.Tweets.CONTENT_URI,
                    null, null, null, sortOrder);
            data = c;
        }
        twitterAdapter = new TwitterAdapter(getApplicationContext(), data, emptyView);
        recyclerView.setAdapter(twitterAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.setAdapter(null);
    }

    public void UpdateUI(String s) {
        twitterData.setText(s);
    }


    private void updateEmptyView() {

        if (twitterAdapter.getItemCount() == 0) {
            TextView ev = (TextView) findViewById(R.id.recycler_view_empty);
            if (null != ev) {

                int message;
                @TwitterTaskService.TweetStatus int tweetStattus = getLocationStatus(this);

                switch (tweetStattus) {
                    case TwitterTaskService.TWEET_STATUS_AUTHO_PROBLEM:
                        message = R.string.empty_autho_problem;
                        break;
                    case TwitterTaskService.TWEET_STATUS_FAILED_CONNECT_SERVER:
                        message = R.string.empty_failed_connect_server;
                        break;
                    case TwitterTaskService.TWEET_STATUS_INVALID_DATA:
                        message = R.string.empty_invalid_data;
                        break;
                    default:
                        message = R.string.empty_list;

                }
                ev.setText(message);
            }

        }
    }

    @SuppressWarnings("ResourceType")
    static public
    @TwitterTaskService.TweetStatus
    int getLocationStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_tweet_status_key), TwitterTaskService.TWEET_STATUS_FAILED_CONNECT_SERVER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}