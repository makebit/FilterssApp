package com.makebit.filterss;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.makebit.filterss.adapters.CollectionListAdapter;
import com.makebit.filterss.fragments.CollectionListFragment;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.RESTMiddleware;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class CollectionManagerActivity extends AppCompatActivity implements CollectionListFragment.CollectionEditInterface {
    private final String TAG = getClass().getName();
    private RESTMiddleware api;

    private ArrayList<Collection> collections;
    private ActionBar actionbar;
    private boolean collectionsChange;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_manager);

        Toolbar toolbar = findViewById(R.id.collection_manager_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.collections);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        api = new RESTMiddleware(this);
        collectionsChange = false;

        //Get a SharedPreferences instance
        UserPrefs prefs = new UserPrefs(this);
        user = prefs.retrieveUser();

        collections = (ArrayList<Collection>) prefs.retrieveCollections();

        showListFragment();
    }

    private void showListFragment() {
        CollectionListFragment collectionListFragment = CollectionListFragment.newInstance(collections);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.collectionListFrameLayout, collectionListFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_multifeed_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            /*if(editView){
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }*/
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the collection is updated
     *
     * @param collection the collection to update
     * @param adapter    the adapter of the list where the collection needed to be updated
     */
    @Override
    public void onUpdateCollection(final Collection collection, final CollectionListAdapter adapter) {
        api.updateUserCollection(user.getToken(), collection.getId(), collection.getTitle(), collection.getColor(), new SQLOperationCallback() {
            @Override
            public void onLoad(Response<SQLOperation> response) {
                if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "updateUserCollection Collection " + collection.getTitle() + " updated");
                    Snackbar.make(findViewById(android.R.id.content), R.string.collection_updated_successfully, Snackbar.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();
                    collectionsChange = true;

                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "updateUserCollection sendArticleFeedback for " + collection.getTitle() + " returned 0, " + response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "updateUserCollection Collection " + collection.getTitle() + " NOT updated" + t.getMessage());
                Snackbar.make(findViewById(android.R.id.content), R.string.collection_update_error, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * The function is called when a collection is deleted. When the API returns a successful
     * response the collection is removed from the list
     *
     * @param collection  the deleted collection
     * @param collections the list of collections to update
     * @param position    the position of the collection in the list
     * @param adapter     the adapter of the collections list
     */
    @Override
    public void onDeleteCollection(final Collection collection, final List<Collection> collections, final int position, final CollectionListAdapter adapter) {
        if (position != 0) {

            api.deleteUserCollection(user.getToken(), collection.getId(), new SQLOperationCallback() {
                @Override
                public void onLoad(Response<SQLOperation> response) {
                    if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Collection " + collection.getTitle() + " removed");
                        collections.remove(position);
                        adapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(android.R.id.content), R.string.collection_removed_successfully, Snackbar.LENGTH_LONG).show();
                        collectionsChange = true;
                    } else {
                        Log.e(ArticleActivity.logTag + ":" + TAG, "Collection " + collection.getTitle() + " NOT removed returned 0 " + response);
                        Snackbar.make(findViewById(android.R.id.content), R.string.collection_remove_error, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Collection " + collection.getTitle() + " NOT removed" + t.getMessage());
                    Snackbar.make(findViewById(android.R.id.content), R.string.collection_remove_error, Snackbar.LENGTH_LONG).show();

                }

            });

        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.not_possible_remove_this_collection, Snackbar.LENGTH_LONG).show();
        }

    }

    /**
     * Used to notify the ArticleListActivity that collections have been changed
     */
    @Override
    public void onBackPressed() {
        if (collectionsChange) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}

