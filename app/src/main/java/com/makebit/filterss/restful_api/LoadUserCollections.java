package com.makebit.filterss.restful_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.User;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.callbacks.CollectionCallback;
import com.makebit.filterss.restful_api.interfaces.AsyncResponse;

import java.util.List;

import retrofit2.Response;

/**
 * Retrieve User's Data from the API: Feeds, FeedGroups, Multifeeds, Collections and persist them
 */
public class LoadUserCollections extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "LoadUserData";

    //Call back interface
    public AsyncResponse delegate = null;

    // The parent context
    private Context parent;

    // The User for which to gather all the info
    private User user;

    private RESTMiddleware api;
    private boolean gotCollections;

    /**
     * Constructor of LoadUserData class
     *
     * @param asyncResponse AsyncTask response listener Callback
     * @param c             Activity Context
     * @param user          Logged User
     */
    public LoadUserCollections(AsyncResponse asyncResponse, Context c, User user) {
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        // Set the parent
        parent = c;
        // Set the user
        this.user = user;

        //Set booleans to default value
        gotCollections = false;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(parent);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        //Get a SharedPreferences instance
        final UserPrefs prefs = new UserPrefs(parent);

        //Gets the list of all the User's Collections
        Log.d(ArticleActivity.logTag + ":" + TAG, "getUserCollections:");
        api.getUserCollections(user.getToken(), new CollectionCallback() {
            @Override
            public void onLoad(Response<List<Collection>> response) {
                List<Collection> collections = response.body();

                if(response.code() == 200 && collections != null){
                    for(Collection collection : collections) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "getUserCollections, Collection: " + collection);
                    }
                    collections.get(0).setTitle(parent.getString(R.string.read_it_later));

                    //Persist the Collections
                    prefs.storeCollections(collections);
                    gotCollections = true;

                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "getUserCollections, Multifeed response is: " + response.code());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Error on getUserCollections: " + t.getMessage());
            }
        });

        //Wait for all the API calls to return
        while (isStillDownloading()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

    /**
     * Get the Global Downloading State
     *
     * @return True if is still downloading False otherwise
     */
    private boolean isStillDownloading() {
        if (gotCollections)
            return false;
        else
            return true;
    }
}