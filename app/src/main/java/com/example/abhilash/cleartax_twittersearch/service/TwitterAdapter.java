package com.example.abhilash.cleartax_twittersearch.service;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.net.ParseException;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhilash.cleartax_twittersearch.API.Statuses;
import com.example.abhilash.cleartax_twittersearch.R;
import com.example.abhilash.cleartax_twittersearch.data.TweetColumns;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Abhilash on 5/23/2016.
 */

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.ViewHolder> {
    private Statuses mSearches;
    private Context mContext;
    private Cursor mCursor;
    private static Typeface robotoMedium;
    private static Typeface robotoBold;
    private static Typeface robotoThin;
    private static Typeface robotoLight;
    View mEmptyView;
    private int rowIdColumn;
    private boolean dataIsValid;

    private DataSetObserver mDataSetObserver;
    public TwitterAdapter(Context context,Cursor cursor,View emptyView) {
        mCursor = cursor;
        mContext = context;
        mEmptyView = emptyView;
    }
    public TwitterAdapter(){

    }

 /*   @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
      //  String id = mCursor.getString(mCursor.getColumnIndex(TweetColumns._ID));

        return mCursor.getLong(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(TweetColumns._ID))));

    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        robotoMedium = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf");
        robotoBold = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Bold.ttf");
        robotoThin = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Thin.ttf");
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        final ViewHolder vh = new ViewHolder(view);
       /* view.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
              //  Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(DetailActivity.this).toBundle();
                mCursor.moveToPosition(position);
                String symbol = mCursor.getString(mCursor.getColumnIndex(TweetColumns.ID));
                Intent stockDetailIntent = new Intent(mContext, DetailActivity.class);
                stockDetailIntent.putExtra("symbol", symbol);
                mContext.startActivity(stockDetailIntent);
            }
        });*/
        return vh;
    }




        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
      //  mSearches.moveToPosition(position);
//        Search search = mSearches.get(position);
       // String sd = search.getText();

        int iu = mCursor.getCount();
        mCursor.moveToPosition(position);
        holder.userName.setText(mCursor.getString(mCursor.getColumnIndex(TweetColumns.NAME)));
        holder.userScreenName.setText(mCursor.getString(mCursor.getColumnIndex(TweetColumns.SCREEN_NAME)));
        String tweet = mCursor.getString(mCursor.getColumnIndex(TweetColumns.TEXT));
        holder.tweetText.setText(tweet);

        String tweetDate = mCursor.getString(mCursor.getColumnIndex(TweetColumns.CREATED_AT));
String sd =tweetDate.substring(0,3)+", "+tweetDate.substring(8,10)+" " /*+ tweetDate.substring(26,30)*/ + tweetDate.substring(4,7)+ " "+tweetDate.substring(26,30)
        +" "+  tweetDate.substring(11,19) +" " +tweetDate.substring(20,25) ;
            /*DateUtils.getRelativeTimeSpanString(
                    mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString()
                    + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR));*/
            /*String dtStart = "2010-10-15T09:27:37Z";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date date = null;
            try {
                date = format.parse(sd);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat zx = new SimpleDateFormat("yyyy-MM-dd");
            Date asas = new Date();
            String datetime = zx.format(date);*/
            String strCurrentDate = "Wed, 18 Apr 2012 07:55:29 +0000";
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.UK);
            Date newDate = null;
            try {
                newDate = format.parse(sd);

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
            format.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

            String date = format.format(newDate);

            holder.createdAt.setText(date);
        Picasso.with(mContext).load(mCursor.getString(mCursor.getColumnIndex(TweetColumns.PROFILE_IMAGE))).into(holder.userProfile);
        holder.userProfile.setContentDescription(tweet);


          }

    @Override
    public int getItemCount () {
            int count = mCursor.getCount();
     mEmptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        return count;
    }


        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == mCursor) {
                return null;
            }
            final Cursor oldCursor = mCursor;
            if (oldCursor != null && mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
            mCursor = newCursor;
            if (mCursor != null) {
                if (mDataSetObserver != null) {
                    mCursor.registerDataSetObserver(mDataSetObserver);
                }
                rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
                dataIsValid = true;
                notifyDataSetChanged();
            } else {
                rowIdColumn = -1;
                dataIsValid = false;
                notifyDataSetChanged();
            }
            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            return oldCursor;
        }


    public static class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        public ImageView userProfile;
        public TextView userName;
        public TextView userScreenName;
        public TextView tweetText;
        public TextView createdAt;


        public ViewHolder(View view) {
            super(view);
            userProfile = (ImageView) view.findViewById(R.id.userId);
            userName = (TextView) view.findViewById(R.id.userName);
            userName.setTypeface(robotoBold);
            userScreenName = (TextView) view.findViewById(R.id.userScreenName);
            userScreenName.setTypeface(robotoLight);
            tweetText = (TextView) view.findViewById(R.id.text);
            tweetText.setTypeface(robotoMedium);
            createdAt = (TextView) view.findViewById(R.id.createdAt);
createdAt.setTypeface(robotoThin);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

