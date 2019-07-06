package com.makebit.filterss.restful_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.User;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.callbacks.MultifeedCallback;
import com.makebit.filterss.restful_api.interfaces.AsyncResponse;

import java.util.List;

import retrofit2.Response;

/**
 * Retrieve User's Multifeed from the API: Feeds, FeedGroups, Multifeeds and persist them
 */
public class LoadUserMultifeeds extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "LoadUMultifeeds";

    //Call back interface
    public AsyncResponse delegate = null;

    // The parent context
    private Context parent;

    // The User for which to gather all the info
    private User user;

    private RESTMiddleware api;
    private boolean gotFeedGroups;
    private boolean gotFeeds;
    private boolean gotMultifeeds;

    /**
     * Constructor of LoadUserData class
     * @param asyncResponse AsyncTask response listener Callback
     * @param c             Activity Context
     * @param user          Logged User
     */
    public LoadUserMultifeeds(AsyncResponse asyncResponse, Context c, User user){
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        // Set the parent
        parent = c;
        // Set the user
        this.user = user;

        //Set booleans to default value
        gotFeedGroups   = false;
        gotFeeds        = false;
        gotMultifeeds   = false;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(parent);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        //Get a SharedPreferences instance
        final UserPrefs prefs = new UserPrefs(parent);

        //Get all the User's FeedGroups
        Log.d(ArticleActivity.logTag + ":" + TAG, "getUserMultifeeds:");
        api.getUserMultifeeds(user.getToken(), new MultifeedCallback() {

            @Override
            public void onLoad(Response<List<Multifeed>> response) {
                List<Multifeed> multifeeds = response.body();

                if(response.code() == 200 && multifeeds != null){
                    for(Multifeed multifeed : multifeeds) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed: " + multifeed);
                    }

                    //Persist the FeedGroups
                    prefs.storeMultifeeds(multifeeds);
                    gotMultifeeds = true;

                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Multifeed response is: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Error on getUserMultifeeds: " + t.getMessage());
            }
        });

        //Wait for all the API calls to return
        while (isStillDownloading()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

    /**
     * Get the Global Downloading State
     * @return True if is still downloading False otherwise
     */
    private boolean isStillDownloading(){
        if(gotMultifeeds)
            return false;
        else
            return true;
    }
}