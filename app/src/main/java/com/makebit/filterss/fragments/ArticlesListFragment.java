package com.makebit.filterss.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.adapters.ArticleRecyclerViewAdapter;
import com.makebit.filterss.controllers.ArticleListSwipeController;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.ArticlesScores;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.RSSFeed;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.UserData;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.RESTMiddleware;
import com.makebit.filterss.restful_api.callbacks.ArticleCallback;
import com.makebit.filterss.restful_api.callbacks.ArticlesScoresCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallbackLocal;
import com.makebit.filterss.restful_api.interfaces.AsyncRSSFeedResponse;
import com.makebit.filterss.rss_parser.LoadRSSFeed;
import com.makebit.filterss.service.SQLiteService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ArticlesListFragment extends Fragment implements ArticleListSwipeController.RecyclerItemTouchHelperListener {
    private final String TAG = getClass().getName();
    private RecyclerView recyclerView = null;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    List<Article> articles;// = Article.generateMockupArticles(25);
    private UserData userData;
    private RESTMiddleware api;
    private int feedCounter;
    private int scoreCounter;
    private Context context;
    private ArticleRecyclerViewAdapter adapter;
    private boolean allFeedsLoaded = true;
    private boolean sqLiteArticlesLoaded = true;

    private List<LoadRSSFeed> loadRSSFeedList = new ArrayList<>();
    private Map<String, Integer> feedArticlesNumberMap = new HashMap<>();
    private boolean onlineRunning;
    private SQLiteService sqLiteService;
    private List<Feed> allFeedList;
    private ArticleListSwipeController itemTouchHelperCallback;
    private SwipeRefreshLayout swipeContainer;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticlesListFragment() {
    }

    public static ArticlesListFragment newInstance(int columnCount) {
        ArticlesListFragment fragment = new ArticlesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new RESTMiddleware(getContext());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view;

        view = inflater.inflate(R.layout.fragment_article_list, container, false);
        recyclerView = view.findViewById(R.id.listArticles);

        if (recyclerView != null) {
            // Create the RecyclerView and Set it's adapter
            Context context = view.getContext();
            this.context = context;
            recyclerView = view.findViewById(R.id.listArticles);

            // Set the swipe controller
            itemTouchHelperCallback = new ArticleListSwipeController(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new ArticleRecyclerViewAdapter(articles, mListener, context);
            recyclerView.setAdapter(adapter);
        }

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateArticles(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                android.R.color.black,
                R.color.colorAccent,
                android.R.color.black);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Log.v("RSSLOG", "Swiping item at pos:" + position);
        mListener.onListFragmentInteractionSwipe(articles.get(position));
    }

    /**
     * Callback launched (on Fragment Attach) from the activity to inform the fragment that the UserData has been loaded
     */
    public synchronized void updateArticles(final boolean swiped) {
        final List<Feed> feedList = new ArrayList<>();
        List<Article> collectionArticleList = new ArrayList<>();
        final long startTimeDownloadArticles = System.nanoTime();
        //Get the Transferred UserData
        this.userData = UserData.getInstance();
        final UserPrefs prefs = new UserPrefs(getContext());
        sqLiteService = SQLiteService.getInstance(getContext());

        //Prepare the articles list, and before downloading the real list of articles, retrieve the list
        //of articles stored in the local SQLite Database;
        if (articles == null) {
            this.articles = new ArrayList<>();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (swipeContainer != null) {
                    swipeContainer.setEnabled(true);
                }
            }
        });


        Collection collection = null;
        //Get the list of feeds to show in the RecyclerView
        //All Multifeeds Feeds Articles
        if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
            feedList.addAll(userData.getFeedList());
        }
        //Multifeed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_MULTIFEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            feedList.addAll(userData.getMultifeedMap().get(multifeed.getId()));
        }
        //Feed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_FEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            feedList.add(userData.getMultifeedList().get(userData.getMultifeedPosition()).getFeeds().get(userData.getFeedPosition()));
        }
        //Collection Articles
        else if (userData.getVisualizationMode() == UserData.MODE_COLLECTION_ARTICLES) {
            collection = userData.getCollectionList().get(userData.getCollectionPosition());
            List<Article> articlesFromCollection = collection.getArticles();
            if (articlesFromCollection != null)
                collectionArticleList.addAll(articlesFromCollection);
            Log.d(ArticleActivity.logTag + ":" + TAG, "Collections: " + collectionArticleList);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (swipeContainer != null) {
                        swipeContainer.setEnabled(false);
                    }
                }
            });
        }

        //MODE_ALL_MULTIFEEDS_FEEDS || MODE_MULTIFEED_ARTICLES || MODE_FEED_ARTICLES
        if (userData.getVisualizationMode() != UserData.MODE_COLLECTION_ARTICLES) {
            /*
             * LOCAL (SQLite)
             */
            // Wait until all the articles stored locally in the SQLite database are retrieved

            sqLiteArticlesLoaded = false;

            final Thread waitSQLiteLoaded = new Thread(new Runnable() {
                @Override
                public void run() {

                    //When the local ArticleList has finished loading, get the new articles in the RecyclerView adapter's list
                    articles.clear();
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Getting articles from DB... Ordering articles by: " + userData.getArticleOrder());

                    if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
                        //List<Article> localArticleList = userData.getLocalArticleList();

                        // get all the articles directly from the database
                        sqLiteService.getFilteredArticles(feedList, userData.getArticleOrder(), new ArticleCallback() {
                            @Override
                            public void onLoad(List<Article> localArticles) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Local article list: " + localArticles.size());

                                articles = localArticles;

                                sqLiteArticlesLoaded = true;
                            }

                            @Override
                            public void onFailure() {
                                Log.e(ArticleActivity.logTag + ":" + TAG, "Failed loading local article list");
                            }
                        });

                    } else {
                        //List<Article> localArticleListFiltered = userData.getLocalArticleListFiltered(feedList);
                        // get filtered articles directly from the database
                        sqLiteService.getFilteredArticles(feedList, userData.getArticleOrder(), new ArticleCallback() {
                            @Override
                            public void onLoad(List<Article> localArticles) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Filtered local article list: " + localArticles.size());

                                articles = localArticles;

                                sqLiteArticlesLoaded = true;
                            }

                            @Override
                            public void onFailure() {
                                Log.e(ArticleActivity.logTag + ":" + TAG, "Failed loading filtered local article list");
                            }
                        });
                    }

                    while (!sqLiteArticlesLoaded) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (articles.size() > 0) {
                        // Local DB not empty
                        //Associate the FeedObjects to each Article (since these are not stored in the local SQLite Database
                        for (final Article article : articles) {
                            for (Feed feed : userData.getFeedList()) {
                                if (feed.getId() == article.getFeed()) {
                                    article.setFeedObj(feed);
                                }
                            }
                            // set the article read information
                            article.setRead(sqLiteService.getArticleRead(article.getHashId()));
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (recyclerView != null) {
                                    recyclerView.setAdapter(new ArticleRecyclerViewAdapter(articles, mListener, getContext()));
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                    itemTouchHelperCallback.resetSwiped();
                                }
                            }
                        });

                        mListener.onListFragmentLocalArticlesReady(articles);
                    } else {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Local DB is empty");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (swipeContainer != null && swipeContainer.isRefreshing()) {
                                    swipeContainer.setRefreshing(false);
                                }
                            }
                        });


                    }
                }
            });
            waitSQLiteLoaded.start();

            /*
             * ONLINE
             */
            if (isNetworkAvailable() && !onlineRunning) {
                // Wait until all LoadRSSFeeds has finished then call onListFragmentArticlesReady
                // to notify that we have something to show
                Date now = new Date();
                Date lastUpdate = prefs.retrieveLastUpdate();
                long diffMin = (now.getTime() - lastUpdate.getTime()) / (60 * 1000);

                Log.d(ArticleActivity.logTag + ":" + TAG, "Last update: " + lastUpdate + ". Diff: " + diffMin + " mins ago");

                if (diffMin >= 10 || swiped) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Starting online fetch...");

                    feedCounter = 0;
                    scoreCounter = 0;
                    onlineRunning = true;
                    allFeedsLoaded = false;

                    // perform the download from all the feeds
                    allFeedList = userData.getFeedList();

                    loadRSSFeedList.clear();

                    for (final Feed feed : allFeedList) {
                        loadRSSFeedList.add(
                                new LoadRSSFeed(new AsyncRSSFeedResponse() {
                                    @Override
                                    public void processFinish(Object output, final RSSFeed rssFeed) {
                                        final List<Article> downloadedArticleList = new ArrayList<>();
                                        final HashMap<String, Article> articlesHashesMap = new HashMap<>();

                                        for (final Article article : rssFeed.getItemList()) {
                                            article.setFeed(feed.getId());
                                            article.setFeedObj(feed);

                                            articlesHashesMap.put(article.getHashId(), article);

                                            //articles.add(article);
                                            downloadedArticleList.add(article);
                                        }

                                        // Get the scores for the set of articles
                                        if (!articlesHashesMap.keySet().isEmpty()) {
                                            getScoreAndSaveArticles(articlesHashesMap, downloadedArticleList);
                                        } else {
                                            Log.d(ArticleActivity.logTag + ":" + TAG, "articlesHashesMap is empty!");
                                            scoreCounter++;
                                        }

                                        //Save the number of articles for each feed, mapped in a HashMap
                                        feedArticlesNumberMap.put(feed.getTitle(), rssFeed.getItemCount());

                                        Log.d(ArticleActivity.logTag + ":" + TAG, "Founded #Articles: " + rssFeed.getItemCount());
                                        Log.d(ArticleActivity.logTag + ":" + TAG, "LoadRSSFeed finished: " + (feedCounter + 1) + "/" + allFeedList.size());
                                        feedCounter++;

                                    }
                                }, getContext(), feed));
                    }

                    //Execute all AsyncTasks
                    for (int i = 0; i < loadRSSFeedList.size(); i++) {
                        loadRSSFeedList.get(i).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    final Thread waitAllFeedsLoaded = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (feedCounter < allFeedList.size() || scoreCounter < allFeedList.size() || !sqLiteArticlesLoaded) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //Time the operation
                            long endTimeLUD = System.nanoTime();
                            long duration = (endTimeLUD - startTimeDownloadArticles);  //divide by 1000000 to get milliseconds.
                            Log.d(ArticleActivity.logTag + ":" + TAG, "#TIME: Downloaded ALL the Feeds Articles: " + (duration / 1000000) + "ms");

                            allFeedsLoaded = true;
                            onlineRunning = false;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (swipeContainer != null && swipeContainer.isRefreshing()) {
                                        swipeContainer.setRefreshing(false);
                                    }
                                }
                            });

                            // save last update value
                            Log.d(ArticleActivity.logTag + ":" + TAG, "Saving last update: " + new Date());
                            prefs.storeLastUpdate(new Date());

                            if (articles.size() == 0) {
                                // local DB empty => reload
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Local DB empty, reloading...");
                                updateArticles(false);

                            } else {
                                //Wait for onCreateView to set RecyclerView's Adapter
                                while (recyclerView == null || recyclerView.getAdapter() == null) {
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                mListener.onListFragmentArticlesReady(swiped);

                                //mListener.onListFragmentAllArticlesReady(feedArticlesNumberMap);
                            }

                        }
                    });
                    waitAllFeedsLoaded.start();

                    // call on list fragmentArticlesReady always after a delay of 60 seconds
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            waitAllFeedsLoaded.interrupt();
                            allFeedsLoaded = true;
                            if (feedCounter < feedList.size()) {
                                if (mListener != null) {
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "TIMEOUT LoadRSSFeed, loading not completed");
                                    mListener.onListFragmentArticlesReady(swiped);
                                    onlineRunning = false;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (swipeContainer != null) {
                                                swipeContainer.setEnabled(false);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }, 60000);

                }
            } else {

                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeContainer != null && swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                    }
                });*/

                if (onlineRunning)
                    Log.d(ArticleActivity.logTag + ":" + TAG, "An online fetch is already running");
                if (!isNetworkAvailable())
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Network not available");
            }
        }
        //MODE_COLLECTION_ARTICLES
        else {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Local articles: " + collectionArticleList);

            articles.clear();
            articles.addAll(collectionArticleList);

            //Notify a change in the RecyclerView's Article List

            final ArticleRecyclerViewAdapter articleRecyclerViewAdapter = new ArticleRecyclerViewAdapter(articles, mListener, getContext());
            articleRecyclerViewAdapter.setCollection(collection);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView != null) {
                        recyclerView.setAdapter(articleRecyclerViewAdapter);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        itemTouchHelperCallback.resetSwiped();
                    }
                }
            });

            mListener.onListFragmentLocalArticlesReady(null);
        }
    }

    private synchronized void computeArticlesScore(List<Article> articles) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Computing articles score...");

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            float articleScoreByRating = article.getScore() * article.getFeedObj().getMultifeed().getRating();
            article.setScore(articleScoreByRating);
        }

    }

    /**
     * Gets the scores for a list of articles hashes
     *
     * @param articlesHashes
     */
    private void getScoreAndSaveArticles(final HashMap<String, Article> articlesHashes,
                                         final List<Article> articlesList) {
        api.getArticlesScores(articlesHashes.keySet(), new ArticlesScoresCallback() {

            @Override
            public void onLoad(Response<List<ArticlesScores>> response) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Response getArticlesScores: " + response.code() + " " + response.body());

                scoreCounter++;
                List<ArticlesScores> articlesScores = response.body();
                if (response.code() == 200 && articlesScores != null) {
                    for (int i = 0; i < articlesScores.size(); i++) {
                        Article article = articlesHashes.get(articlesScores.get(i).getArticle());
                        Log.d(ArticleActivity.logTag + ":" + TAG, "getArticlesScores Received score for article: " + articlesScores.get(i) + " : Multifeed rating: " + article.getFeedObj().getMultifeed().getRating());
                        article.setScore(articlesScores.get(i).getScore());
                    }

                    saveArticles(articlesList);
                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "getArticlesScores Response getArticlesScores: " + response.code());

                    saveArticles(articlesList);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                scoreCounter++;

                saveArticles(articlesList);

                Log.e(ArticleActivity.logTag + ":" + TAG, "getArticlesScores Scores for articles " + articlesHashes.keySet() + "NOT received: " + t.getMessage());
            }

        });
    }

    private void saveArticles(List<Article> articlesList) {
        computeArticlesScore(articlesList);

        // save downloaded articles in the db
        manageArticlesLocalDBPersistence(sqLiteService, articlesList, new SQLOperationCallbackLocal() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG,
                        "Added " + sqlOperation.getAffectedRows() + " articles into the local SQLite DB, Article Table!");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Failed saving to local DB: " + t.getMessage());
            }

        });
    }

    /**
     * Query if there is Internet Connection or the device is Offline
     *
     * @return True if there is Internet connection, false if Offline
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // save everything to the database
    void manageArticlesLocalDBPersistence(final SQLiteService sqLiteService,
                                          final List<Article> downloadedArticleList, final SQLOperationCallbackLocal callback) {
        sqLiteService.putArticles(downloadedArticleList, callback);
    }

    public ArticleRecyclerViewAdapter getAdapter() {
        return (ArticleRecyclerViewAdapter) recyclerView.getAdapter();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void onListFragmentInteractionClick(Article article);

        void onListFragmentInteractionSwipe(Article article);

        // Notify the ArticleListActivity that articles from the local storage have been loaded
        void onListFragmentLocalArticlesReady(final List<Article> articleList);

        // Notify the ArticleListActivity that new articles from the online feeds have been loaded
        void onListFragmentArticlesReady(boolean swiped);

        //void onListFragmentAllArticlesReady(Map<String, Integer> feedArticlesNumberMap);
    }

}
