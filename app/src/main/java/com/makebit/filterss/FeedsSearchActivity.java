package com.makebit.filterss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.makebit.filterss.adapters.FeedsListAdapter;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;
import com.makebit.filterss.models.UserData;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.RESTMiddleware;
import com.makebit.filterss.restful_api.callbacks.FeedCallback;
import com.makebit.filterss.restful_api.callbacks.MultifeedCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;

import java.util.List;

import retrofit2.Response;

public class FeedsSearchActivity extends AppCompatActivity {
    public static final String LANG_EN = "EN";
    public static final String LANG_IT = "IT";
    public static final String FEED_LINK = "com.makebit.filterss.feedlink";
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private UserData userData;

    private static final int REQUEST_CREATE_MULTIFEED = 0;

    private DrawerLayout drawerLayout;
    private FeedsListAdapter adapter;
    private NavigationView navigationView;

    private List<Multifeed> multifeeds;
    private List<Feed> feeds;
    private ListView feedsListview;
    private boolean multifeedsChange;
    private Feed selectedFeed;
    private DialogInterface selectedDialog;
    private View selectedView;
    private String lang;
    private User user;
    private int createFeedSelectedMultifeed;
    private View viewDialogAdd;
    private AlertDialog.Builder createNewFeedDialog;
    private EditText titleEditText;
    private EditText linkEditText;

    // view https://developer.android.com/training/improving-layouts/smooth-scrolling#java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_search);

        user = new UserPrefs(this).retrieveUser();

        api = new RESTMiddleware(this);
        multifeedsChange = false;

        loadUserData();

        Toolbar toolbar = findViewById(R.id.feeds_search_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        // getting current language
        String locale = userData.getLocale().getLanguage();
        Log.d(ArticleActivity.logTag + ":" + TAG, "Locale: " + locale);

        switch (locale) {
            case "en":
                lang = LANG_EN;
                break;
            case "it":
                lang = LANG_IT;
                break;
            default:
                lang = LANG_EN;
        }

        api.getAllFeeds(lang, new FeedCallback() {
            @Override
            public void onLoad(List<Feed> feedsReply) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "All Feeds Loaded: " + feedsReply.size());
                feeds = feedsReply;
                setFeedsList();
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "All Feeds Error");
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout_feeds_search);
        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        // Navigation view with list of categories
        navigationView = findViewById(R.id.nav_view_categories);
        // Set the first item ("All") as checked
        navigationView.getMenu().getItem(0).setChecked(true);
        // Set click listener, when new cat is clicked change filter the dataset
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Update the UI based on the item selected
                        final String category = (String) menuItem.getTitle();
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Looking for " + category + " feeds");

                        // If the category is All get all the feeds otherwise get the feed by category
                        if (category.equals("All") || category.equals("Tutti")) {
                            api.getAllFeeds(lang, new FeedCallback() {
                                @Override
                                public void onLoad(List<Feed> feedsReply) {
                                    updateFeedsList(feedsReply);
                                }

                                @Override
                                public void onFailure() {
                                    Log.e(ArticleActivity.logTag + ":" + TAG, "All Feeds Error");
                                }
                            });
                        } else {
                            api.getFilteredFeeds(lang, null, category, new FeedCallback() {
                                @Override
                                public void onLoad(List<Feed> feedsReply) {
                                    updateFeedsList(feedsReply);
                                }

                                @Override
                                public void onFailure() {
                                    Log.e(ArticleActivity.logTag + ":" + TAG, "Feeds for " + category + " error");
                                }
                            });
                        }

                        return true;
                    }
                });

    }

    private void loadUserData() {
        if (userData == null) {
            //Get a UserData instance
            userData = UserData.getInstance();
            userData.loadPersistedData(this);
            userData.processUserData();
        }
    }

    /**
     * Initializes the feed list setting the adapter and the click listener on a list's item
     */
    private void setFeedsList() {
        // Get the feed list
        feedsListview = findViewById(R.id.listViewFeedsList);
        // Set the adapter
        adapter = new FeedsListAdapter(this, feeds, userData.getFeedMap(), false);
        feedsListview.setAdapter(adapter);
        // On item click open the dialog for adding the feed to a multifeed
        feedsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Feed feed = adapter.getItem(position);
                Log.d(ArticleActivity.logTag + ":" + TAG, "Feed " + id + " clicked. Info: " + feed.toString());

                // This dialog allows the user to add a feed into a multifeed display a SingleChoiceItems list
                new AlertDialog.Builder(FeedsSearchActivity.this)
                        .setTitle(R.string.dialog_add_feed_title)
                        .setSingleChoiceItems(Multifeed.toStrings(multifeeds), -1,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedIndex) {
                                        Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed " + selectedIndex + " clicked, info: " + multifeeds.get(selectedIndex).toString());
                                        addFeedToMultifeed(feed, multifeeds.get(selectedIndex), dialog, view);
                                    }
                                })
                        // The positive button allows the creation of a new multifeed
                        .setPositiveButton(R.string.dialog_add_feed_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedFeed = feed;
                                selectedDialog = dialog;
                                selectedView = view;
                                startMultifeedCreationActivity();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Dialog closed");
                            }
                        })
                        .show();
            }

        });
        feedsListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Feed feed = adapter.getItem(position);
                Log.d(ArticleActivity.logTag + ":" + TAG, "Feed " + id + " clicked. Info: " + feed.toString());

                openWebPage(feed.getWebsite());

                return true;
            }
        });

    }

    public void openWebPage(String url) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.URL, url);
        startActivity(intent);
    }

    /**
     * Update the list with the new feedsReply, used when filtering feeds
     *
     * @param feedsReply The feeds returned that has to be showed in the feed list
     */
    private void updateFeedsList(List<Feed> feedsReply) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Feeds updated: " + feedsReply.size());
        feeds = feedsReply;
        adapter.updateFeeds(feeds);    // update the feeds in the adapter
        feedsListview.setSelection(0); // set scroll position to 0
    }

    /**
     * onResume update the user's multifeed refreshing the list from userData
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list of multifeed saved locally
        Log.d(ArticleActivity.logTag + ":" + TAG, "Refreshing User's Multifeed...");

        api.getUserMultifeeds(user.getToken(), new MultifeedCallback() {
            @Override
            public void onLoad(Response<List<Multifeed>> response) {
                List<Multifeed> mf = response.body();

                if (response.code() == 200 && mf != null) {
                    // Refresh the list of multifeed saved locally
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Refreshing User's Multifeed done");
                    multifeeds = mf;

                    // check if intent receive with feed url
                    checkFeedUrlReceived();
                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "getUserMultifeeds returned response code " + response.code());
                    multifeeds = mf;
                }


            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "getUserMultifeeds, Refreshing User's Multifeed FAILED " + t.getMessage());
            }

        });
    }

    private void checkFeedUrlReceived() {
        Bundle extra = getIntent().getExtras();

        if (extra != null && extra.getString(FeedsSearchActivity.FEED_LINK) != null) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Intent received starting feed creation");
            String feedLink = getIntent().getExtras().getString(FeedsSearchActivity.FEED_LINK);
            createNewFeed(feedLink);
        }
    }

    /**
     * Add the selected feed to the user's multifeed
     *
     * @param feed      The selected feed that has to be added the the selected multifeed
     * @param multifeed The multifeed where the feed has to be added
     * @param dialog    The dialog showed to the user during the adding operation
     * @param view
     */

    private void addFeedToMultifeed(final Feed feed, final Multifeed multifeed, final DialogInterface dialog, final View view) {
        api.addUserFeed(user.getToken(), feed.getId(), multifeed.getId(), new SQLOperationCallback() {
            @Override
            public void onLoad(Response<SQLOperation> response) {
                dialog.dismiss();

                if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "addFeedToMultifeed Feed " + feed.getId() + " added to multifeed " + multifeed.getId());

                    ImageView imageViewAdd = view.findViewById(R.id.imageViewFeedsSearchActionIcon);
                    imageViewAdd.animate().setDuration(500).rotation(45);

                    Snackbar.make(findViewById(android.R.id.content), feed.getTitle() + " " +
                            getText(R.string.saved_in) + " " + multifeed.getTitle(), Snackbar.LENGTH_LONG).show();

                    multifeedsChange = true;

                } else {
                    if (response.code() == 409) {
                        Log.e(ArticleActivity.logTag + ":" + TAG, "addFeedToMultifeed Feed " + feed.getId() + " to multifeed " + multifeed.getId() + " duplicate, " + response);
                        Snackbar.make(findViewById(android.R.id.content), R.string.feed_already_added, Snackbar.LENGTH_LONG).show();
                    } else {
                        Log.e(ArticleActivity.logTag + ":" + TAG, "addFeedToMultifeed Feed " + feed.getId() + " to multifeed " + multifeed.getId() + " returned 0, " + response);
                        Snackbar.make(findViewById(android.R.id.content), R.string.error_adding_feed, Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();

                Log.e(ArticleActivity.logTag + ":" + TAG, "addFeedToMultifeed Feed " + feed.getId() + " NOT added to multifeed " + multifeed.getId() + " " + t.getMessage());

                Snackbar.make(findViewById(android.R.id.content), R.string.error_adding_feed, Snackbar.LENGTH_LONG).show();
            }
        });


    }


    /**
     * Start the activity for the creation of a new Multifeed
     */
    private void startMultifeedCreationActivity() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Starting multifeed creation...");
        Intent intent = new Intent(this, MultifeedCreationActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_MULTIFEED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CREATE_MULTIFEED) {
            if (resultCode == RESULT_OK) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from MultifeedCreationActivity with RESULT_OK");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_saved, Snackbar.LENGTH_LONG).show();
                multifeedsChange = true;

                Multifeed createdMultifeed = (Multifeed) data.getSerializableExtra(MultifeedCreationActivity.CREATED_MULTIFEED_EXTRA);
                addFeedToMultifeed(selectedFeed, createdMultifeed, selectedDialog, selectedView);

            } else {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from MultifeedCreationActivity without RESULT_OK");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_not_saved, Snackbar.LENGTH_LONG).show();

            }
        }
    }

    private void createNewFeed(String uriPath) {
        // This dialog allows the user to add a new feed into a multifeed display a SingleChoiceItems list
        viewDialogAdd = LayoutInflater.from(FeedsSearchActivity.this).inflate(R.layout.dialog_create_new_feed, null);
        titleEditText = viewDialogAdd.findViewById(R.id.editTextFeedAddName);
        linkEditText = viewDialogAdd.findViewById(R.id.editTextFeedAddLink);

        if (uriPath != null && !uriPath.isEmpty()) linkEditText.setText(uriPath);

        createFeedSelectedMultifeed = -1;

        if (createNewFeedDialog == null) {
            createNewFeedDialog = new AlertDialog.Builder(FeedsSearchActivity.this)
                    .setView(viewDialogAdd)
                    .setTitle(R.string.dialog_create_new_feed_title)
                    .setSingleChoiceItems(Multifeed.toStrings(multifeeds), -1,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int selectedIndex) {
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed " + selectedIndex + " clicked, info: " + multifeeds.get(selectedIndex).toString());
                                    createFeedSelectedMultifeed = selectedIndex;
                                }
                            })
                    // The positive button allows the creation of a new multifeed
                    .setPositiveButton(R.string.dialog_crete_new_feed_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            final String title = titleEditText.getText().toString();
                            String link = linkEditText.getText().toString();
                            Log.d(ArticleActivity.logTag + ":" + TAG, "Feed creation " + title + " " + link + " " + createFeedSelectedMultifeed);

                            if (title.equals("") || link.equals("") || createFeedSelectedMultifeed < 0) {
                                // error
                                dialog.dismiss();
                                Snackbar.make(findViewById(android.R.id.content), R.string.error_creating_feed, Snackbar.LENGTH_LONG).show();
                            } else {
                                api.createNewFeed(user.getToken(), multifeeds.get(createFeedSelectedMultifeed).getId(), title, link, new SQLOperationCallback() {
                                    @Override
                                    public void onLoad(Response<SQLOperation> response) {
                                        if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                                            Log.d(ArticleActivity.logTag + ":" + TAG, "createNewFeed Feed created and added");
                                            Snackbar.make(findViewById(android.R.id.content), title + " " +
                                                    getText(R.string.saved_in) + " " + multifeeds.get(createFeedSelectedMultifeed).getTitle(), Snackbar.LENGTH_LONG).show();
                                            multifeedsChange = true;
                                            createNewFeedDialog = null;
                                        } else {
                                            Log.e(ArticleActivity.logTag + ":" + TAG, "createNewFeed for " + title + " returned 0, " + response);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Log.e(ArticleActivity.logTag + ":" + TAG, "createNewFeed, feed NOT created " + t.getMessage());
                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(ArticleActivity.logTag + ":" + TAG, "Dialog closed");
                        }
                    });
            createNewFeedDialog.show();
        }
    }

    private void setNavigationViewLang(int menu) {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(menu);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.itemLanguageSelectorEN:
                lang = LANG_EN;
                setNavigationViewLang(R.menu.drawer_view_categories_en);
                item.setChecked(!item.isChecked());
                drawerLayout.openDrawer(Gravity.END);
                return true;
            case R.id.itemLanguageSelectorIT:
                lang = LANG_IT;
                setNavigationViewLang(R.menu.drawer_view_categories_it);
                item.setChecked(!item.isChecked());
                drawerLayout.openDrawer(Gravity.END);
                return true;
            case R.id.itemAddNewFeed:
                createNewFeed(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeds_search, menu);

        if (lang.equals(LANG_EN)) {
            menu.findItem(R.id.itemLanguageSelectorEN).setChecked(true);
            setNavigationViewLang(R.menu.drawer_view_categories_en);
        } else if (lang.equals(LANG_IT)) {
            menu.findItem(R.id.itemLanguageSelectorIT).setChecked(true);
            setNavigationViewLang(R.menu.drawer_view_categories_it);
        }

        MenuItem searchItem = menu.findItem(R.id.itemSearchFeeds);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return onQueryTextSubmit(newText);
            }
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("multifeedsChange", multifeedsChange);
        if(viewDialogAdd != null) {
            savedInstanceState.putString("titleEditText", titleEditText.getText().toString());
            savedInstanceState.putString("linkEditText", linkEditText.getText().toString());
            savedInstanceState.putInt("createFeedSelectedMultifeed", createFeedSelectedMultifeed);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Get Bundle saved data
        multifeedsChange = (boolean) savedInstanceState.getSerializable("multifeedsChange");
        if(viewDialogAdd != null) {
            titleEditText.setText(savedInstanceState.getString("titleEditText", ""));
            linkEditText.setText(savedInstanceState.getString("linkEditText", ""));
            createFeedSelectedMultifeed = savedInstanceState.getInt("createFeedSelectedMultifeed",-1);
        }
    }

    /**
     * Used to notify the ArticleListActivity that collections have been changed
     */
    @Override
    public void onBackPressed() {
        if (multifeedsChange) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
