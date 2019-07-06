package com.makebit.filterss.restful_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.callbacks.CollectionCallback;
import com.makebit.filterss.restful_api.callbacks.MultifeedCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallbackLocal;
import com.makebit.filterss.restful_api.interfaces.AsyncResponse;
import com.makebit.filterss.service.SQLiteService;

import java.util.List;

import retrofit2.Response;

/**
 * Retrieve User's Data from the API: Feeds, FeedGroups, Multifeeds, Collections and persist them
 */
public class LoadUserData extends AsyncTask<Void, Void, Integer> {
    private static final int MAX_TIMEOUT_MS = 10000;
    private final String TAG = getClass().getName();

    //AsyncTask Results
    public static int DATA_LOADING_FAILED     = 0;
    public static int DATA_LOADING_TERMINATED = 1;

    //Call back interface
    public AsyncResponse delegate = null;

    // The parent context
    private Context parent;

    // The User for which to gather all the info
    private User user;

    private RESTMiddleware api;
    private boolean gotMultifeeds;
    private boolean gotCollections;
    private boolean authFaild;
    private boolean error;

    /**
     * Constructor of LoadUserData class
     * @param asyncResponse AsyncTask response listener Callback
     * @param c             Activity Context
     * @param user          Logged User
     */
    public LoadUserData(AsyncResponse asyncResponse, Context c, User user){
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        // Set the parent
        parent = c;
        // Set the user
        this.user = user;

        //Set booleans to default value
        gotMultifeeds   = false;
        gotCollections  = false;
        authFaild       = false;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(parent);
    }

    @Override
    protected Integer doInBackground(final Void... params) {
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
                    error = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Error on getUserMultifeeds: " + t.getMessage());
                gotMultifeeds = true;
                error = true;
            }
        });

        //Gets the list of all the User's Collections
        Log.d(ArticleActivity.logTag + ":" + TAG, "getUserCollections:");
        api.getUserCollections(user.getToken(), new CollectionCallback() {
            @Override
            public void onLoad(Response<List<Collection>> response) {
                List<Collection> collections = response.body();

                if(response.code() == 200 && collections != null){
                    for(Collection collection : collections) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Collection: " + collection);
                    }
                    collections.get(0).setTitle(parent.getString(R.string.read_it_later));

                    //Persist the Collections
                    prefs.storeCollections(collections);
                    gotCollections = true;

                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Multifeed response is: " + response.code());
                    error = true;
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Error on getUserCollections: " + t.getMessage());
                error = true;
            }
        });

        SQLiteService sqLiteService = SQLiteService.getInstance(parent);
        sqLiteService.deleteOldArticles(prefs.retrieveFeeds(), new SQLOperationCallbackLocal() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "deleteOldArticles: " + sqlOperation.getAffectedRows() + " articles deleted from the local SQLite DB");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "deleteOldArticles: " + t.getMessage());
            }
        });

        //Wait for all the API calls to return
        int sleepFor = 0;
        while (isStillDownloading() && sleepFor < MAX_TIMEOUT_MS && !error){
            try {
                sleepFor+=100;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Return result
        if(sleepFor >= MAX_TIMEOUT_MS || error)
            return DATA_LOADING_FAILED;
        else
            return DATA_LOADING_TERMINATED;
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
        if(gotMultifeeds && gotCollections)
            return false;
        else
            return true;
    }
}