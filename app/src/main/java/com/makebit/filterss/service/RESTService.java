package com.makebit.filterss.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makebit.filterss.BuildConfig;
import com.makebit.filterss.models.ArticlesScores;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;
import com.makebit.filterss.restful_api.callbacks.ArticlesScoresCallback;
import com.makebit.filterss.restful_api.callbacks.CollectionCallback;
import com.makebit.filterss.restful_api.callbacks.FeedCallback;
import com.makebit.filterss.restful_api.callbacks.MultifeedCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;
import com.makebit.filterss.restful_api.callbacks.UserCallback;
import com.makebit.filterss.restful_api.interfaces.ArticlesRESTInterface;
import com.makebit.filterss.restful_api.interfaces.AuthenticationRESTInterface;
import com.makebit.filterss.restful_api.interfaces.CategoryRESTInterface;
import com.makebit.filterss.restful_api.interfaces.FeedsRESTInterface;
import com.makebit.filterss.restful_api.interfaces.UserRESTInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTService {
    private final String TAG = getClass().getName();


    private static RESTService instance;
    private CategoryRESTInterface categoryRESTInterface;
    private AuthenticationRESTInterface authenticationRESTInterface;
    private FeedsRESTInterface feedsRESTInterface;
    private UserRESTInterface userRESTInterface;
    private final ArticlesRESTInterface articlesRESTInterface;

    private RESTService(Context context) {
        /*Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();*/

        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        Retrofit retrofit;
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .cache(cache)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(RESTConstants.API_BASEURL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(RESTConstants.API_BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        categoryRESTInterface = retrofit.create(CategoryRESTInterface.class);
        authenticationRESTInterface = retrofit.create(AuthenticationRESTInterface.class);
        feedsRESTInterface = retrofit.create(FeedsRESTInterface.class);
        userRESTInterface = retrofit.create(UserRESTInterface.class);
        articlesRESTInterface = retrofit.create(ArticlesRESTInterface.class);
    }

    public static synchronized RESTService getInstance(Context context) {
        if (instance == null) {
            instance = new RESTService(context);
        }
        return instance;
    }


    /*********************** Auth *********************************/

    /**
     * Gets the authenticated User
     *
     * @param email    Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void getUserAuthentication(String email, String password, final UserCallback callback) {
        authenticationRESTInterface.getUserAuthentication(email, password).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Registration of a new User
     *
     * @param name     User's name
     * @param surname  User's surname
     * @param email    Email used by the user for the registration
     * @param password Password used to protect the account
     * @param callback Callback for API response management
     */
    public void registerNewUser(String name, String surname, String email, String password, String locale, final SQLOperationCallback callback) {
        authenticationRESTInterface.registerNewUser(name, surname, email, password, locale).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /**
     * Manage the login with a Google account
     *
     * @param token    the token of the Google account
     * @param callback Callback for API response management
     */
    public void getUserAuthenticationGoogle(String googleToken, final UserCallback callback) {
        final List<User> users = new ArrayList<>();

        authenticationRESTInterface.getUserAuthenticationGoogle(googleToken).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /*********************** Feeds *********************************/

    /**
     * Gets the list of all the Feeds
     *
     * @param callback Callback for API response management
     */
    public void getAllFeeds(String lang, final FeedCallback callback) {
        final List<Feed> feeds = new ArrayList<>();

        feedsRESTInterface.getAllFeeds(lang)
                .enqueue(new retrofit2.Callback<List<Feed>>() {
                    @Override
                    public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                        for (Feed feed : response.body()) {
                            feeds.add(feed);
                        }

                        callback.onLoad(feeds);
                    }

                    @Override
                    public void onFailure(Call<List<Feed>> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Gets the list of all the Filtered Feeds (Search)
     *
     * @param searchFilter String containing the string pattern used to filter/search the desired feeds, if any
     * @param category     String containing the cateogry, if any
     * @param callback     Callback for API response management
     */
    public void getFilteredFeeds(String lang, String searchFilter, String category, final FeedCallback callback) {
        final List<Feed> feeds = new ArrayList<>();

        if (category != null) {
            feedsRESTInterface.getFilteredFeedsBySearchAndCategory(lang, searchFilter, category)
                    .enqueue(new retrofit2.Callback<List<Feed>>() {
                        @Override
                        public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                            for (Feed feed : response.body()) {
                                feeds.add(feed);
                            }

                            callback.onLoad(feeds);
                        }

                        @Override
                        public void onFailure(Call<List<Feed>> call, Throwable t) {
                            callback.onFailure();
                        }
                    });
            return;
        }

        if (searchFilter != null) {
            feedsRESTInterface.getFilteredFeedsBySearch(searchFilter)
                    .enqueue(new retrofit2.Callback<List<Feed>>() {

                        @Override
                        public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                            for (Feed feed : response.body()) {
                                feeds.add(feed);
                            }

                            callback.onLoad(feeds);
                        }

                        @Override
                        public void onFailure(Call<List<Feed>> call, Throwable t) {
                            callback.onFailure();
                        }
                    });
            return;
        }


        if (category != null) {
            feedsRESTInterface.getFilteredFeedsByCategory(category)
                    .enqueue(new retrofit2.Callback<List<Feed>>() {
                        @Override
                        public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                            for (Feed feed : response.body()) {
                                feeds.add(feed);
                            }

                            callback.onLoad(feeds);
                        }

                        @Override
                        public void onFailure(Call<List<Feed>> call, Throwable t) {
                            callback.onFailure();
                        }
                    });
            return;
        }
    }


    /**
     * Add a Feed to a User's Multifeed
     *
     * @param feed      feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback  Callback for API response management
     */
    public void addUserFeed(String token, int feed, int multifeed, final SQLOperationCallback callback) {
        userRESTInterface.addUserFeed(token, feed, multifeed).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /**
     * Delete a Feed related to a User's Multifeed
     *
     * @param feed      feed to associate to the multifeed
     * @param multifeed multifeed of the user, to witch to associate the feed
     * @param callback  Callback for API response management
     */
    public void deleteUserFeed(String token, int feed, int multifeed, final SQLOperationCallback callback) {
        userRESTInterface.deleteUserFeed(token, feed, multifeed).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void createNewFeed(String token, int multifeed, String title, String link, final SQLOperationCallback callback) {
        userRESTInterface.createNewFeed(token, multifeed, title, link).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /*********************** User - Multifeeds *********************************/

    /**
     * Gets the list of all the User's Multifeeds
     *
     * @param userEmail Collection's owner email
     * @param callback  Callback for API response management
     */
    public void getUserMultifeeds(String token, final MultifeedCallback callback) {
        userRESTInterface.getUserMultifeeds(token)
                .enqueue(new retrofit2.Callback<List<Multifeed>>() {
                    @Override
                    public void onResponse(Call<List<Multifeed>> call, Response<List<Multifeed>> response) {
                        callback.onLoad(response);
                    }

                    @Override
                    public void onFailure(Call<List<Multifeed>> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    /**
     * Add a Multifeed to a User
     *
     * @param title    Title of the Multifeed
     * @param user     Owner of the multifeed
     * @param color    Color used to indicate the multifeed on user's app
     * @param callback Callback for API response management
     */
    public void addUserMultifeed(String token, String title, int color, int rating, final SQLOperationCallback callback) {
        userRESTInterface.addUserMultifeed(token, title, color, rating).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Delete a Multifeed related to a User
     *
     * @param id       Multifeed's identification number
     * @param callback Callback for API response management
     */
    public void deleteUserMultifeed(String token, int multifeed, final SQLOperationCallback callback) {
        userRESTInterface.deleteUserMultifeed(token, multifeed).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Update a Multifeed with a certain id
     *
     * @param id       Multifeed's Id
     * @param newTitle The new title to set instead of the old one
     * @param newColor The new color to set instead of the old one
     */
    public void updateUserMultifeed(String token, int multifeed, String title, int color, int rating, final SQLOperationCallback callback) {
        userRESTInterface.updateUserMultifeed(token, multifeed, title, color, rating).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /*********************** User - Collections *********************************/

    /**
     * Gets the list of all the User's Collections
     *
     * @param userEmail Colelction's owner email
     * @param callback  Callback for API response management
     */
    public void getUserCollections(String token, final CollectionCallback callback) {
        userRESTInterface.getUserCollections(token)
                .enqueue(new retrofit2.Callback<List<Collection>>() {

                    @Override
                    public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                        callback.onLoad(response);
                    }

                    @Override
                    public void onFailure(Call<List<Collection>> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    /**
     * Add a Collection to a User
     *
     * @param title    Title of the Collection
     * @param color    Color used to indicate the Collection on user's app
     * @param callback Callback for API response management
     */
    public void addUserCollection(String token, String title, int color, final SQLOperationCallback callback) {
        userRESTInterface.addUserCollection(token, title, color).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Delete a Collection related to a User, due to table relations, this api also needs to delete all
     * the saved_articles associated to this collection(this is the first thing done).
     *
     * @param id       Collection's identification number
     * @param callback SQLOperationListCallback for API response management:
     *                 - List's first SQLOperation indicates the rows deleted from saved_article table;
     *                 - List's second SQLOperation indicates the rows deleted from the collection table;
     */
    public void deleteUserCollection(String token, int id, final SQLOperationCallback callback) {
        final List<SQLOperation> sqlOperationList = new ArrayList<>();

        userRESTInterface.deleteUserCollection(token, id).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Update a Collection with a certain id
     *
     * @param id       Collection's Id
     * @param newTitle The new title to set instead of the old one
     * @param newColor The new color to set instead of the old one
     */
    public void updateUserCollection(String token, int collection, String title, int color, final SQLOperationCallback callback) {
        userRESTInterface.updateUserCollection(token, collection, title, color).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Add an Article and a SavedArticle(Association with a collection). The OnResponse will return with
     * two SQLperation results stored in a list (the first one is related to the Article inserption, while
     * the second SQLOperation refers to the SavedArticle insertion). Article insertion does not fail on duplicate
     * hash_id (if already present on the DB, then only the Saved article will be inserted)
     *
     * @param title
     * @param description
     * @param comment
     * @param link
     * @param img_link
     * @param pub_date
     * @param userId
     * @param feedId
     * @param collectionId
     * @param callback     SQLOperationListCallback callback interface
     */
    public void addArticleToCollection(String token, String title, String description, String link, String img_link,
                                       String pub_date, int feed, int collection, final SQLOperationCallback callback) {
        userRESTInterface.addArticleToCollection(token, title, description, link, img_link, pub_date, feed, collection)
                .enqueue(new retrofit2.Callback<SQLOperation>() {

                    @Override
                    public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                        callback.onLoad(response);
                    }

                    @Override
                    public void onFailure(Call<SQLOperation> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    /**
     * Delete a SavedArticle related to a User
     *
     * @param article
     * @param collection
     * @param callback   Callback for API response management
     */
    public void deleteArticleFromCollection(String token, String article, int collection, final SQLOperationCallback callback) {
        userRESTInterface.deleteArticleFromCollection(token, article, collection).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /**
     * Add an Article's Opened to the User
     *
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void addUserOpenedArticle(String token, String article, final SQLOperationCallback callback) {
        userRESTInterface.addUserOpenedArticle(token, article).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Add an Article's Read to the User
     *
     * @param user
     * @param article
     * @param callback Callback for API response management
     */
    public void addUserReadArticle(String token, String article, final SQLOperationCallback callback) {
        userRESTInterface.addUserReadArticle(token, article).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * Add an Article's Feedback to the User
     *
     * @param user
     * @param article
     * @param vote
     * @param callback Callback for API response management
     */
    public void addUserFeedbackArticle(String token, String article, int vote, final SQLOperationCallback callback) {
        userRESTInterface.addUserFeedbackArticle(token, article, vote).enqueue(new retrofit2.Callback<SQLOperation>() {

            @Override
            public void onResponse(Call<SQLOperation> call, Response<SQLOperation> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<SQLOperation> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /*********************** Articles *********************************/
    /**
     * Gets the scores for a list of articles
     *
     * @param articlesHashes
     * @param callback       Callback for API response management
     */
    public void getArticlesScores(List<String> articlesHashes, final ArticlesScoresCallback callback) {
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("articles", articlesHashes);

        articlesRESTInterface.getArticlesScores(hashMap).enqueue(new retrofit2.Callback<List<ArticlesScores>>() {

            @Override
            public void onResponse(Call<List<ArticlesScores>> call, Response<List<ArticlesScores>> response) {
                callback.onLoad(response);
            }

            @Override
            public void onFailure(Call<List<ArticlesScores>> call, Throwable t) {
                callback.onFailure(t);
            }


        });
    }

}
