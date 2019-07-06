package com.makebit.filterss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.makebit.filterss.adapters.FeedsListAdapter;
import com.makebit.filterss.fragments.MultifeedEditFragment;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;
import com.makebit.filterss.models.UserData;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.RESTMiddleware;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;

import java.util.List;

import retrofit2.Response;

public class MultifeedCreationActivity extends AppCompatActivity implements MultifeedEditFragment.MultifeedEditInterface {
    public static final String CREATED_MULTIFEED_EXTRA = "CREATED_MULTIFEED";
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private UserData userData;
    private UserPrefs prefs;


    private MultifeedEditFragment multifeedEditFragment;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multifeed_creation);

        prefs = new UserPrefs(this);
        user = prefs.retrieveUser();

        loadUserData();

        api = new RESTMiddleware(this);

        Toolbar toolbar = findViewById(R.id.multifeed_creation_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        multifeedEditFragment = MultifeedEditFragment.newInstance(null);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.multifeedEditFrameLayout, multifeedEditFragment);
        ft.commit();


    }

    private void loadUserData(){
        if(userData == null) {
            //Get a UserData instance
            userData = UserData.getInstance();
            userData.loadPersistedData(this);
            userData.processUserData();
        }
    }

    @Override
    public void onUpdateMultifeed(Multifeed multifeed) {
        // Do nothing because the user clicked on the back button
    }

    @Override
    public void onDeleteFeed(Multifeed multifeed, Feed feed, List<Feed> feeds, int position, FeedsListAdapter adapter) {
        // Do nothing because the list of feed is empty (we are creating a new multifeed)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_multifeed_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Back clicked
                finish();
                return true;
            case R.id.itemSaveMultifeed: // Save clicked
                createNewMultifeed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void createNewMultifeed() {
        // The user clicked on the save icon
        // get the multifeed from the fragment
        final Multifeed multifeed = multifeedEditFragment.getMultifeed();

        // Save the multifeed and finish
        final Intent returnIntent = new Intent();

        if(validMultifeed(multifeed)){

            Log.d(ArticleActivity.logTag + ":" + TAG, "Saving newly created multifeed, info: " + multifeed.toString());

            // Call the API and add the user
            api.addUserMultifeed(user.getToken(), multifeed.getTitle(), multifeed.getColor(), multifeed.getRating(), new SQLOperationCallback() {
                @Override
                public void onLoad(Response<SQLOperation> response) {
                    if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "addUserMultifeed Multifeed saved successfully via API " + response.toString());

                        // return with success code
                        multifeed.setId(response.body().getInsertId());
                        returnIntent.putExtra(CREATED_MULTIFEED_EXTRA, multifeed);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Log.e(ArticleActivity.logTag + ":" + TAG, "addUserMultifeed returned 0, " + response);
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "addUserMultifeed Multifeed not saved " + t.getMessage());
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }

            });

        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }



    }

    public boolean validMultifeed(Multifeed multifeed) {
        boolean valid = true;

        String title = multifeed.getTitle();

        if (title.isEmpty()) {
            valid = false;
        }

        return valid;
    }
}
