package com.makebit.filterss.service;


import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.persistence.articles.ArticleCursor;
import com.makebit.filterss.persistence.articles.ArticleSQLiteRepository;
import com.makebit.filterss.restful_api.callbacks.ArticleCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallbackLocal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteService {
    private final String TAG = getClass().getName();
    private static SQLiteService instance;
    private ArticleSQLiteRepository articleSQLiteRepository;
    private final Lock _mutex = new ReentrantLock(true);

    private SQLiteService(Context context) {
        articleSQLiteRepository = new ArticleSQLiteRepository(context);
    }

    public static synchronized SQLiteService getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteService(context);
        }
        return instance;
    }

    /**
     * Get/Retrieve the List of all the articles in the local SQLite Database (from the Article Table)
     *
     * @param callback ArticleCallback callback object with the onLoad() and onFailed() methods
     *                 that are used to notify the result of the execution. This callback returns
     *                 a List<Article> on the onLoad() method.
     */
    public synchronized void getAllArticles(final int orderBy, final ArticleCallback callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                ArticleCursor cursor = articleSQLiteRepository.findAll(orderBy);
                if (cursor != null) {
                    List<Article> articleList = getArticleListFromCursor(cursor);
                    callback.onLoad(articleList);
                } else
                    callback.onFailure();
            }
        };
        thread.start();
    }

    /**
     * Get/Retrieve the List of filtered articles in the local SQLite Database (from the Article Table)
     *
     * @param callback ArticleCallback callback object with the onLoad() and onFailed() methods
     *                 that are used to notify the result of the execution. This callback returns
     *                 a List<Article> on the onLoad() method.
     */
    public synchronized void getFilteredArticles(final List<Feed> feedList, final int orderBy, final ArticleCallback callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String feedIdsString = "";
                for (Feed feed : feedList) {
                    feedIdsString += feed.getId() + ",";
                }
                if (!feedList.isEmpty())
                    feedIdsString = feedIdsString.substring(0, feedIdsString.length() - 1);

                ArticleCursor cursor = articleSQLiteRepository.findAllFiltered(orderBy, feedIdsString);
                if (cursor != null) {
                    List<Article> articleList = getArticleListFromCursor(cursor);
                    callback.onLoad(articleList);
                } else
                    callback.onFailure();
            }
        };
        thread.start();

        /*getAllArticles(orderBy, new ArticleCallback() {
            @Override
            public void onLoad(List<Article> articleList) {
                List<Article> feedFilteredArticles = new ArrayList<>();
                for (Article article : articleList) {
                    for (Feed feed : feedList) {
                        if (article.getFeed() == feed.getId())
                            feedFilteredArticles.add(article);
                    }
                }
                callback.onLoad(feedFilteredArticles);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });*/
    }

    /**
     * Put/Store a list of articles in the local SQLite Database (into the Article Table)
     *
     * @param articles List<Article> object containing the list of articles passed as argument
     * @param callback SQLOperationCallback callback object with the onLoad() and onFailed() methods
     *                 that are used to notify the result of the execution. This callback returns
     *                 a SQLOperation object on the onLoad() method, containing informations about
     *                 the query ran.
     */
    public synchronized void putArticles(final List<Article> articles, final SQLOperationCallbackLocal callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    //_mutex.lock();
                    articleSQLiteRepository.batchAdd(articles);
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setAffectedRows(articles.size());
                    sqlOperation.setMessage("Successfully added a list of Articles(" + articles.size() + ") on the database Article table!");
                    callback.onLoad(sqlOperation);
                    //_mutex.unlock();
                } catch (Exception e) {
                    Log.e(ArticleActivity.logTag + ":SQL", "getArticleListFromCursor: " + e.getMessage());
                    callback.onFailure(e);
                }
            }
        };
        thread.start();
    }

    /**
     * Delete all the articles from the local SQLite Database (from the Article Table)
     *
     * @param callback SQLOperationCallback callback object with the onLoad() and onFailed() methods
     *                 that are used to notify the result of the execution. This callback returns
     *                 a SQLOperation object on the onLoad() method, containing informations about
     *                 the query ran.
     */
    public synchronized void deleteAllArticles(final SQLOperationCallbackLocal callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    articleSQLiteRepository.deleteAll();
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setMessage("Deleted all the rows in the sqlite database!");
                    callback.onLoad(sqlOperation);
                } catch (SQLException e) {
                    callback.onFailure(e);
                }
            }
        };
        thread.start();
    }

    /**
     * Delete an articles from the local SQLite Database (from the Article Table)
     *
     * @param article  Article object to be deleted from the database
     * @param callback SQLOperationCallback callback object with the onLoad() and onFailed() methods
     *                 that are used to notify the result of the execution. This callback returns
     *                 a SQLOperation object on the onLoad() method, containing informations about
     *                 the query ran.
     */
    public synchronized void deleteArticle(final Article article, final SQLOperationCallbackLocal callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    articleSQLiteRepository.delete(article);
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setMessage("Deleted an Article(" + article.getHashId() + ") in the sqlite database!");
                    callback.onLoad(sqlOperation);
                } catch (SQLException e) {
                    callback.onFailure(e);
                }
            }
        };
        thread.start();
    }

    /**
     * A Helper function, that given an ArticleCursor object, it retrieves and returns a list of articles(List<Article>)
     *
     * @param cursor ArticleCursor object
     * @return articles    List<Article> object
     */
    public List<Article> getArticleListFromCursor(ArticleCursor cursor) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final List<Article> articles = new ArrayList<>();

        //Iterate with the cursor and fill the list of articles
        while (cursor.moveToNext()) {
            try {
                Article newArticle = new Article();
                newArticle.setHashId(cursor.getHashId());
                newArticle.setTitle(cursor.getTitle());
                newArticle.setDescription(cursor.getDescription());
                newArticle.setComment(cursor.getComment());
                newArticle.setLink(cursor.getLink());
                newArticle.setImgLink(cursor.getImageLink());
                newArticle.setPubDate(sdf.parse(cursor.getPubDate()));
                newArticle.setFeed(cursor.getFeed());
                newArticle.setScore(cursor.getScore());
                articles.add(newArticle);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(ArticleActivity.logTag + ":SQL", "getArticleListFromCursor: " + e.getMessage());
            }
        }

        cursor.close();

        return articles;
    }

    public void setArticleRead(final String hashId, final SQLOperationCallbackLocal callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    articleSQLiteRepository.setRead(hashId);
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setMessage("Updating read information for " + hashId + ") in the sqlite database!");
                    callback.onLoad(sqlOperation);
                } catch (SQLException e) {
                    callback.onFailure(e);
                }
            }
        };
        thread.start();
    }

    public boolean getArticleRead(final String hashId) {
        boolean read = articleSQLiteRepository.getRead(hashId);
        return read;
    }

    public Article getArticle(String articleHashId) {
        return articleSQLiteRepository.getArticle(articleHashId);
    }

    public synchronized void deleteOldArticles(final List<Feed> feedList, final SQLOperationCallbackLocal callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    articleSQLiteRepository.deleteOldArticles(feedList);
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setMessage("Deleted old rows in the sqlite database!");
                    callback.onLoad(sqlOperation);
                } catch (SQLException e) {
                    callback.onFailure(e);
                }
            }
        };
        thread.start();
    }
}
