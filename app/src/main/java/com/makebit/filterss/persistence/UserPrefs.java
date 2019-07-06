package com.makebit.filterss.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.FeedGrouping;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.SavedArticle;
import com.makebit.filterss.models.User;
import com.makebit.filterss.models.UserData;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * stores the user object in SharedPreferences
 */
public class UserPrefs {

    /**
     * This application's preferences label
     */
    private static final String PREFS_NAME = "com.company.rss.rss.persistence.UserPrefs";

    /**
     * JSON Object Labels
     */
    private static final String USER_OBJECT = "LoggedUser";
    private static final String FEEDGROUP_LIST_OBJECT = "FeedGroupList";
    private static final String FEED_LIST_OBJECT = "FeedList";
    private static final String MULTIFEED_LIST_OBJECT = "MultifeedList";
    private static final String COLLECTION_LIST_OBJECT = "CollectionList";
    private static final String SAVEDARTICLES_LIST_OBJECT = "SavedArticlesList";
    private static final String ARTICLES_LIST_OBJECT = "ArticleList";
    private static final String LAST_UPDATE = "LastUpdate";
    private static final String ARTICLES_ORDER_BY = "ArticlesOrderBy";
    private static final String FIRST_START = "FirstStart";

    /**
     * This application's preferences
     */
    private static SharedPreferences settings;

    /**
     * This application's settings editor
     */
    private static SharedPreferences.Editor editor;

    /**
     * Constructor takes an android.content.Context argument
     **/
    public UserPrefs(Context ctx) {
        if (settings == null) {
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        /*
         * Get a SharedPreferences editor instance.
         * SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        editor = settings.edit();
    }

    /***************************************************
     *                      STORE
     **************************************************/
    /**
     * Store a User in the Shared Preferences
     *
     * @param user
     */
    public void storeUser(User user) {
        // convert User object user to JSON format
        Gson gson = new Gson();
        String user_json = gson.toJson(user);

        // store in SharedPreferences
        editor.putString(USER_OBJECT, user_json);
        editor.commit();
    }

    /**
     * Store a FeedGroup List in the Shared Preferences
     *
     * @param feedGroups a list of FeedGrouping Objects
     */
    public void storeFeedGroups(List<FeedGrouping> feedGroups) {
        // convert to JSON format
        Gson gson = new Gson();
        String fg_json = gson.toJson(feedGroups);

        // store in SharedPreferences
        editor.putString(FEEDGROUP_LIST_OBJECT, fg_json);
        editor.commit();
    }

    /**
     * Store a Feed List in the Shared Preferences
     *
     * @param feeds a list of Feed Objects
     */
    public void storeFeeds(List<Feed> feeds) {
        // convert to JSON format
        Gson gson = new Gson();
        String f_json = gson.toJson(feeds);

        // store in SharedPreferences
        editor.putString(FEED_LIST_OBJECT, f_json);
        editor.commit();
    }

    /**
     * Store a Feed in the Feeds list Shared Preferences
     *
     * @param feed the feed to add to the list
     */
    public void storeFeed(Feed feed) {
        List<Feed> feeds = retrieveFeeds();
        feeds.add(feed);

        // convert to JSON format
        Gson gson = new Gson();
        String f_json = gson.toJson(feeds);

        // store in SharedPreferences
        editor.putString(FEED_LIST_OBJECT, f_json);
        editor.commit();
    }

    /**
     * Store a Multifeed List in the Shared Preferences
     *
     * @param multifeeds a list of Multifeed Objects
     */
    public void storeMultifeeds(List<Multifeed> multifeeds) {
        // convert to JSON format
        Gson gson = new Gson();
        String mf_json = gson.toJson(multifeeds);

        // store in SharedPreferences
        editor.putString(MULTIFEED_LIST_OBJECT, mf_json);
        editor.commit();
    }

    /**
     * Add a Multifeed to the Multifeed list in the Shared Preferences
     *
     * @param multifeed the multifeed to add to the list
     */
    public void storeMultifeed(Multifeed multifeed) {
        List<Multifeed> multifeeds = retrieveMultifeeds();
        multifeeds.add(multifeed);

        // convert to JSON format
        Gson gson = new Gson();
        String mf_json = gson.toJson(multifeeds);

        // store in SharedPreferences
        editor.putString(MULTIFEED_LIST_OBJECT, mf_json);
        editor.commit();
    }

    /**
     * Store a Collection List in the Shared Preferences
     *
     * @param collections a list of Collection Objects
     */
    public void storeCollections(List<Collection> collections) {
        // convert to JSON format
        Gson gson = new Gson();
        String c_json = gson.toJson(collections);

        // store in SharedPreferences
        editor.putString(COLLECTION_LIST_OBJECT, c_json);
        editor.commit();
    }

    /**
     * Store a SavedArticle List in the Shared Preferences
     *
     * @param savedArticles a list of Collection Objects
     */
    public void storeSavedArticles(List<SavedArticle> savedArticles) {
        // convert to JSON format
        Gson gson = new Gson();
        String sa_json = gson.toJson(savedArticles);

        // store in SharedPreferences
        editor.putString(SAVEDARTICLES_LIST_OBJECT, sa_json);
        editor.commit();
    }

    /**
     * Store a Article List in the Shared Preferences
     *
     * @param articles a list of Collection Objects
     */
    public void storeArticles(List<Article> articles) {
        // convert to JSON format
        Gson gson = new Gson();
        String a_json = gson.toJson(articles);

        // store in SharedPreferences
        editor.putString(ARTICLES_LIST_OBJECT, a_json);
        editor.commit();
    }

    public void storeLastUpdate(Date date) {
        // store in SharedPreferences
        editor.putString(LAST_UPDATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date));
        editor.commit();
    }

    public void storeArticlesOrderBy(int orderBy) {
        // store in SharedPreferences
        editor.putInt(ARTICLES_ORDER_BY, orderBy);
        editor.commit();
    }

    public void storeIsFirstStart(boolean firstStart) {
        // store in SharedPreferences
        editor.putBoolean(FIRST_START, firstStart);
        editor.commit();
    }


    /***************************************************
     *                    RETRIEVE
     **************************************************/
    /**
     * Retrive a User from the Shared Preferences
     *
     * @return User read
     */
    public User retrieveUser() {
        Gson gson = new Gson();
        String usersJson = settings.getString(USER_OBJECT, "");
        return gson.fromJson(usersJson, User.class);
    }

    /**
     * Retrive the FeedGroup List from the Shared Preferences
     *
     * @return A list of FeedGrouping Objects
     */
    public List<FeedGrouping> retrieveFeedGroups() {
        Gson gson = new Gson();
        String feedGroupsJson = settings.getString(FEEDGROUP_LIST_OBJECT, "");
        Type type = new TypeToken<List<FeedGrouping>>() {
        }.getType();
        List<FeedGrouping> result = gson.fromJson(feedGroupsJson, type);
        return result == null ? new ArrayList<FeedGrouping>() : result;
    }

    /**
     * Retrive the Feed List from the Shared Preferences
     *
     * @return A list of Feed Objects
     */
    public List<Feed> retrieveFeeds() {
        Gson gson = new Gson();
        String feedsJson = settings.getString(FEED_LIST_OBJECT, "");
        Type type = new TypeToken<List<Feed>>() {
        }.getType();
        List<Feed> result = gson.fromJson(feedsJson, type);
        return result == null ? new ArrayList<Feed>() : result;
    }

    /**
     * Retrive the Multifeed List from the Shared Preferences
     *
     * @return A list of Multifeed Objects
     */
    public List<Multifeed> retrieveMultifeeds() {
        Gson gson = new Gson();
        String multifeedsJson = settings.getString(MULTIFEED_LIST_OBJECT, "");

        Type type = new TypeToken<List<Multifeed>>() {
        }.getType();
        List<Multifeed> result = gson.fromJson(multifeedsJson, type);
        return result == null ? new ArrayList<Multifeed>() : result;
    }

    /**
     * Retrive the Collection List from the Shared Preferences
     *
     * @return A list of Collection Objects
     */
    public List<Collection> retrieveCollections() {
        Gson gson = new Gson();
        String collectionsJson = settings.getString(COLLECTION_LIST_OBJECT, "");
        Type type = new TypeToken<List<Collection>>() {
        }.getType();
        List<Collection> result = gson.fromJson(collectionsJson, type);
        return result == null ? new ArrayList<Collection>() : result;
    }

    /**
     * Retrive the SavedArticle List from the Shared Preferences
     *
     * @return A list of SavedArticle Objects
     */
    public List<SavedArticle> retrieveSavedArticles() {
        Gson gson = new Gson();
        String savedArticlesJson = settings.getString(SAVEDARTICLES_LIST_OBJECT, "");
        Type type = new TypeToken<List<SavedArticle>>() {
        }.getType();
        List<SavedArticle> result = gson.fromJson(savedArticlesJson, type);
        return result == null ? new ArrayList<SavedArticle>() : result;
    }

    /**
     * Retrive the Article List from the Shared Preferences
     *
     * @return A list of Article Objects
     */
    public List<Article> retrieveArticles() {
        Gson gson = new Gson();
        String articlesJson = settings.getString(ARTICLES_LIST_OBJECT, "");
        Type type = new TypeToken<List<Article>>() {
        }.getType();
        List<Article> result = gson.fromJson(articlesJson, type);
        return result == null ? new ArrayList<Article>() : result;
    }

    /**
     * Retrive the last article update from the Shared Preferences
     *
     * @return The date of the last update
     */
    public Date retrieveLastUpdate() {
        String lastUpdate = settings.getString(LAST_UPDATE, "");
        if (lastUpdate != null && lastUpdate.isEmpty())
            return new Date(0L);
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(lastUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date(0L);
        }
    }

    /**
     * Retrive the set articles order from the Shared Preferences
     *
     * @return The article order
     */
    public int retrieveArticlesOrderBy() {
        return settings.getInt(ARTICLES_ORDER_BY, UserData.ORDER_BY_DATE);
    }

    /**
     * Retrive if the app is started from the first time from the Shared Preferences
     *
     * @return true if first start false otherwise
     */
    public boolean retrieveIsFirstStart() {
        return settings.getBoolean(FIRST_START, true);
    }

    /***************************************************
     *                    REMOVE
     **************************************************/
    /**
     * Remove a all from the Shared Preferences
     *
     */
    public void removeAll() {
        // Remove from SharedPreferences
        editor.remove(USER_OBJECT);
        editor.remove(FEEDGROUP_LIST_OBJECT);
        editor.remove(FEED_LIST_OBJECT);
        editor.remove(MULTIFEED_LIST_OBJECT);
        editor.remove(COLLECTION_LIST_OBJECT);
        editor.remove(SAVEDARTICLES_LIST_OBJECT);
        editor.remove(ARTICLES_LIST_OBJECT);
        editor.remove(LAST_UPDATE);
        editor.remove(ARTICLES_ORDER_BY);
        editor.remove(FIRST_START);
        editor.commit();
    }

}