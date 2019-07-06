package com.makebit.filterss.restful_api;

import android.content.Context;

import com.makebit.filterss.restful_api.callbacks.ArticlesScoresCallback;
import com.makebit.filterss.restful_api.callbacks.CollectionCallback;
import com.makebit.filterss.restful_api.callbacks.FeedCallback;
import com.makebit.filterss.restful_api.callbacks.MultifeedCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;
import com.makebit.filterss.restful_api.callbacks.UserCallback;
import com.makebit.filterss.service.RESTService;

import java.util.ArrayList;
import java.util.Set;


public class RESTMiddleware {
    private Context context;

    /**
     * Simple Constructor
     */
    public RESTMiddleware() {
    }

    /**
     * Complete Constructor
     * @param context
     */
    public RESTMiddleware(Context context) {
        this.context = context;
    }


    /********************************************************************
     *                           Auth
     ********************************************************************/
    /**
     * Gets the authenticated User
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void getUserAuthentication(String email, String password, UserCallback callback){
        RESTService.getInstance(context).getUserAuthentication(email, password, callback);
    }


    /**
     * Registration of a new User
     * @param name User's name
     * @param surname User's surname
     * @param email Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void registerNewUser(String name, String surname, String email, String password, String locale, SQLOperationCallback callback){
        RESTService.getInstance(context).registerNewUser(name, surname, email, password, locale, callback);
    }

    /**
     * Manage the login with a Google account
     * @param token the token of the Google account
     * @param callback Callback for API response management
     */
    public void getUserAuthenticationGoogle(String googleToken, UserCallback callback){
        RESTService.getInstance(context).getUserAuthenticationGoogle(googleToken, callback);
    }

    /********************************************************************
     *                           Feeds
     ********************************************************************/
    /**
     * Gets the list of all the Feeds
     * @param callback Callback for API response management
     */
    public void getAllFeeds(String lang, FeedCallback callback){
        RESTService.getInstance(context).getAllFeeds(lang, callback);
    }

    /**
     * Gets the list of all the Filtered Feeds (Search)
     * @param searchFilter String containing the string pattern used to filter/search the desired feeds, if any
     * @param category String containing the category, if any
     * @param callback Callback for API response management
     */
    public void getFilteredFeeds(String lang, String searchFilter, String category, FeedCallback callback){
        RESTService.getInstance(context).getFilteredFeeds(lang, searchFilter, category, callback);
    }

    /********************************************************************
     *                        User - Feeds
     ********************************************************************/
    /**
     * Add a Feed to a User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback Callback for API response management
     */
    public void addUserFeed(String token, int feed, int multifeed, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserFeed(token, feed, multifeed, callback);
    }

    public void createNewFeed(String token, int multifeed, String title, String link, SQLOperationCallback callback){
        RESTService.getInstance(context).createNewFeed(token, multifeed, title, link, callback);
    }
    /**
     * Delete a Feed related to a User's Multifeed
     * @param feed feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback Callback for API response management
     */
    /*public void deleteUserFeed(int feed, int multifeed, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserFeed(feed, multifeed, callback);
    }*/

    public void deleteUserFeed(String token, int feed, int multifeed, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserFeed(token, feed, multifeed, callback);
    }


    /********************************************************************
     *                       User - Multifeeds
     ********************************************************************/
    /**
     * Gets the list of all the User's Multifeeds
     * @param userEmail Multifeed's owner email
     * @param callback Callback for API response management
     */
    public void getUserMultifeeds(String token, MultifeedCallback callback){
        RESTService.getInstance(context).getUserMultifeeds(token, callback);
    }

    /**
     * Add a Multifeed to a User
     * @param title Title of the Multifeed
     * @param user Owner of the multifeed
     * @param color Color used to indicate the multifeed on user's app
     * @param callback Callback for API response management
     */
    public void addUserMultifeed(String token, String title, int color, int rating, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserMultifeed(token, title, color, rating, callback);
    }

    /**
     * Delete a Multifeed related to a User
     * @param id Multifeed's identification number
     * @param callback Callback for API response management
     */
    /*public void deleteUserMultifeed(int id, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserMultifeed(id, callback);
    }*/
    public void deleteUserMultifeed(String token, int multifeed, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserMultifeed(token, multifeed, callback);
    }

    /**
     * Update a Multifeed with a certain id
     * @param id        Multifeed's identification number
     * @param newTitle  The new title to set instead of the old one
     * @param newColor  The new color to set instead of the old one
     */
    /*public void updateUserMultifeed(int id, String newTitle, int newColor, int newRating, SQLOperationCallback callback){
        RESTService.getInstance(context).updateUserMultifeed(id, newTitle, newColor, newRating, callback);
    }*/

    public void updateUserMultifeed(String token, int multifeed, String title, int color, int rating, SQLOperationCallback callback){
        RESTService.getInstance(context).updateUserMultifeed(token, multifeed, title, color, rating, callback);
    }

    /********************************************************************
     *                       User - Collections
     ********************************************************************/
    /**
     * Gets the list of all the User's Collections
     * @param token Colelction's owner token
     * @param callback Callback for API response management
     */
    public void getUserCollections(String token, CollectionCallback callback){
        RESTService.getInstance(context).getUserCollections(token, callback);
    }

    /**
     * Add a Collection to a User
     * @param title Title of the Collection
     * @param user Owner of the Collection
     * @param color Color used to indicate the Collection on user's app
     * @param callback Callback for API response management
     */
    public void addUserCollection(String token, String title, int color, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserCollection(token, title, color, callback);
    }

    /**
     * Delete a Collection related to a User, due to table relations, this api also needs to delete all
     * the saved_articles associated to this collection(this is the first thing done).
     * @param id Collection's identification number
     * @param callback SQLOperationListCallback for API response management:
     *                 - List's first SQLOperation indicates the rows deleted from saved_article table;
     *                 - List's second SQLOperation indicates the rows deleted from the collection table;
     */
    public void deleteUserCollection(String token, int collection, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteUserCollection(token, collection, callback);
    }

    /**
     * Update a Collection with a certain id
     * @param id        Collection's identification number
     * @param newTitle  The new title to set instead of the old one
     * @param newColor  The new color to set instead of the old one
     */
    public void updateUserCollection(String token, int collection, String title, int color, SQLOperationCallback callback){
        RESTService.getInstance(context).updateUserCollection(token, collection, title, color, callback);
    }


    /********************************************************************
     *                       User - SavedArticles
     ********************************************************************/
    /**
     * Add an Article and a SavedArticle(Association with a collection). The OnResponse will return with
     * two SQLperation results stored in a list (the first one is related to the Article inserption, while
     * the second SQLOperation refers to the SavedArticle insertion). Article insertion does not fail on duplicate
     * hash_id (if already present on the DB, then only the Saved article will be inserted)
     * @param title
     * @param description
     * @param comment
     * @param link
     * @param img_link
     * @param pub_date
     * @param userId
     * @param feedId
     * @param collectionId
     * @param callback      SQLOperationListCallback callback interface
     */
    public void addArticleToCollection(String token, String title, String description, String link, String img_link,
                                       String pub_date, int feed, int collection, SQLOperationCallback callback){
        RESTService.getInstance(context).addArticleToCollection(token, title, description, link, img_link, pub_date, feed, collection, callback);
    }


    /**
     * Delete a SavedArticle related to a User
     * @param article
     * @param collection
     * @param callback Callback for API response management
     */
    public void deleteArticleFromCollection(String token, String article, int collection, SQLOperationCallback callback){
        RESTService.getInstance(context).deleteArticleFromCollection(token, article, collection, callback);
    }

    /**
     * Add an Article's Opened to the User
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void addUserOpenedArticle(String token, String article, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserOpenedArticle(token, article, callback);
    }

    /**
     * Add an Article's Read to the User
     * @param user
     * @param article
     * @param i
     * @param callback Callback for API response management
     */
    public void addUserReadArticle(String token, String article, SQLOperationCallback callback){
        RESTService.getInstance(context).addUserReadArticle(token, article, callback);
    }

    /**
     * Add an Article's Feedback to the User
     * @param token
     * @param article
     * @param vote
     * @param callback Callback for API response management
     */
    public void addUserFeedbackArticle(String token, String article, int vote, SQLOperationCallback callback) {
        RESTService.getInstance(context).addUserFeedbackArticle(token, article, vote, callback);
    }

    /********************************************************************
     *                       Articles
     ********************************************************************/
    /**
     * Gets the scores for a list of articles
     * @param articlesHashes
     * @param callback Callback for API response management
     */
    public void getArticlesScores(Set<String> articlesHashes, ArticlesScoresCallback callback) {
        RESTService.getInstance(context).getArticlesScores(new ArrayList<String>(articlesHashes), callback);
    }
}
