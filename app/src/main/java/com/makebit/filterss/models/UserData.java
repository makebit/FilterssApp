package com.makebit.filterss.models;

import android.content.Context;
import android.util.Log;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.persistence.UserPrefs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This Class is the Model of all the User's Data collected in a single class,
 * the reason being to access and precess the data more easily
 */
public class UserData {
    private final String TAG = getClass().getName();

    //Singleton Instance
    private static final UserData ourInstance = new UserData();

    //Unprocessed Data Lists
    private List<Feed> feedList;
    private List<Multifeed> multifeedList;
    private List<Collection> collectionList;

    //Maps
    private Map<Integer, List<Feed>> multifeedMap;
    private Map<Integer, Feed> feedMap;
    private Map<Integer, List<Article>> collectionMap;

    //AllFeedArticles, AllMultifeedArticles, FeedArticles, CollectionArticles ArticleList Visualization
    public static int MODE_ALL_MULTIFEEDS_FEEDS = 0;
    public static int MODE_MULTIFEED_ARTICLES = 1;
    public static int MODE_FEED_ARTICLES = 2;
    public static int MODE_COLLECTION_ARTICLES = 3;
    public static int ORDER_BY_DATE = 0;
    public static int ORDER_BY_RATING = 1;
    private int visualizationMode;
    private int multifeedPosition;
    private int feedPosition;
    private int collectionPosition;
    private UserPrefs prefs;
    private Context context;
    private Locale locale;

    /**
     * Get Singleton's instance
     *
     * @return
     */
    public static UserData getInstance() {
        return ourInstance;
    }

    /**
     * Simple Constructor
     */
    public UserData() {
        //Init of Hash Maps
        multifeedMap = new HashMap<>();
        feedMap = new HashMap<>();
        collectionMap = new HashMap<>();

        //Default visualization Mode
        visualizationMode = MODE_ALL_MULTIFEEDS_FEEDS;

        locale = Locale.getDefault();
    }

    /**
     * Load all the User's persisted data, which includes:
     * - The logged User
     * - the list of User's Feed-Multifeed Groups (FeedGroups)
     * - The list of User's Feeds
     * - The list of User's Multifeeds
     * - The list of User's Collections
     * - The list of User's Article-Collection Groups (SavedArticles)
     * - The list of User's Articles
     *
     * @param context Activity's context
     */
    public void loadPersistedData(Context context) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "loadPersistedData");

        //Get a SharedPreferences instance
        this.context = context;
        prefs = new UserPrefs(context);

        //Get the User data
        feedList = prefs.retrieveFeeds();
        multifeedList = prefs.retrieveMultifeeds();
        collectionList = prefs.retrieveCollections();
    }

    /**
     * Process the User's Data and Build the class's Map. These associations are much clearer than the raw tables, which associations are
     * defined by a ER-Model and are not easy to correlate.
     */
    public void processUserData() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "processUserData");

        processMultifeedMap();
        processFeedMap();
        processCollectionMap();
    }

    /**
     * Process the User's Data and Build a Multifeed Map (Multifeed --> List<Feed>).
     * It iterates all the user's multifeeds, and for each of them finds which feeds are
     * associated.
     */
    public void processMultifeedMap() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "processMultifeedMap");

        multifeedMap.clear();

        for (Multifeed multifeed : multifeedList) {
            multifeedMap.put(multifeed.getId(), multifeed.getFeeds());
            for (Feed feed : multifeed.getFeeds()) {
                feed.setMultifeed(multifeed);
            }
            feedList.addAll(multifeed.getFeeds());
        }
    }

    public void processFeedMap() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "processFeedMap");

        feedMap.clear();

        for (Feed feed : feedList) {
            feedMap.put(feed.getId(), feed);
        }
    }

    /**
     * Process the User's Data and Build a Collection Map (Collection --> List<Article>).
     * It iterates all the user's multifeeds, and for each of them finds which feeds are
     * associated.
     */
    public void processCollectionMap() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "processCollectionMap");

        collectionMap.clear();

        for (Collection collection : collectionList) {
            collectionMap.put(collection.getId(), collection.getArticles());
            for (Article article : collection.getArticles()) {
                article.setFeedObj(feedMap.get(article.getFeed()));
            }
        }
    }

    /********************************************/


    public Map<Integer, List<Feed>> getMultifeedMap() {
        return multifeedMap;
    }

    public Map<Integer, Feed> getFeedMap() {
        return feedMap;
    }

    public Map<Integer, List<Article>> getCollectionMap() {
        return collectionMap;
    }

    public List<Feed> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<Feed> feedList) {
        this.feedList = feedList;
    }

    public List<Multifeed> getMultifeedList() {
        return multifeedList;
    }

    public void setMultifeedList(List<Multifeed> multifeedList) {
        this.multifeedList = multifeedList;
    }

    public List<Collection> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(List<Collection> collectionList) {
        this.collectionList = collectionList;
    }

    public int getVisualizationMode() {
        return visualizationMode;
    }

    public void setVisualizationMode(int visualizationMode) {
        this.visualizationMode = visualizationMode;
    }

    public int getMultifeedPosition() {
        return multifeedPosition;
    }

    public void setMultifeedPosition(int multifeedPosition) {
        this.multifeedPosition = multifeedPosition;
    }

    public int getFeedPosition() {
        return feedPosition;
    }

    public void setFeedPosition(int feedPosition) {
        this.feedPosition = feedPosition;
    }

    public int getCollectionPosition() {
        return collectionPosition;
    }

    public void setCollectionPosition(int collectionPosition) {
        this.collectionPosition = collectionPosition;
    }

    public int getArticleOrder() {
        return prefs.retrieveArticlesOrderBy();
    }

    public void setArticleOrder(int articleOrder) {
        prefs.storeArticlesOrderBy(articleOrder);
    }

    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (feedList != null ? !feedList.equals(userData.feedList) : userData.feedList != null)
            return false;
        if (multifeedList != null ? !multifeedList.equals(userData.multifeedList) : userData.multifeedList != null)
            return false;
        if (multifeedMap != null ? !multifeedMap.equals(userData.multifeedMap) : userData.multifeedMap != null)
            return false;
        return collectionMap != null ? collectionMap.equals(userData.collectionMap) : userData.collectionMap == null;
    }

    @Override
    public int hashCode() {
        int result = (feedList != null ? feedList.hashCode() : 0);
        result = 31 * result + (multifeedList != null ? multifeedList.hashCode() : 0);
        result = 31 * result + (collectionList != null ? collectionList.hashCode() : 0);
        result = 31 * result + (multifeedMap != null ? multifeedMap.hashCode() : 0);
        result = 31 * result + (collectionMap != null ? collectionMap.hashCode() : 0);
        return result;
    }

    /**
     * Delete all the persisted data
     */
    public void deleteAll() {
        prefs.removeAll();
        prefs = new UserPrefs(context);
    }

    public List<String> getMapFeedTitlesListByKey(Multifeed multifeed) {
        List<String> titles = new ArrayList<>();
        if (multifeedMap != null && multifeed != null) {
            for (Feed f : multifeed.getFeeds()) {
                titles.add(f.getTitle());
            }
        }
        return titles;
    }

    public List<String> getMapFeedIconLinkListByKey(Multifeed multifeed) {
        List<String> icons = new ArrayList<>();
        if (multifeedMap != null && multifeed != null) {
            for (Feed f : multifeed.getFeeds()) {
                icons.add(f.getIconURL());
            }
        }
        return icons;
    }
}
