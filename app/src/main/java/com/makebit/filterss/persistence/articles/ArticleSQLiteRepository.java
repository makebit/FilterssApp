package com.makebit.filterss.persistence.articles;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.UserData;
import com.makebit.filterss.persistence.articles.ArticleDBContract.ArticleEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.makebit.filterss.persistence.articles.ArticleDBContract.getWritableDatabase;


public class ArticleSQLiteRepository {
    private final String TAG = getClass().getName();
    private static final int MAX_ARTICLES_TO_KEEP = 200;

    private SQLiteDatabase db;

    public ArticleSQLiteRepository(Context context) {
        db = getWritableDatabase(context);
    }

    public void batchAdd(List<Article> articles) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        db.beginTransaction();
        try {
            for (Article article : articles) {
                if (article.getTitle() != null) {
                    db.execSQL("INSERT OR REPLACE INTO " + ArticleEntry.TABLE_NAME
                                    + " (" + ArticleEntry.HASH_ID_CLMN + ","
                                    + ArticleEntry.TITLE_CLMN + ","
                                    + ArticleEntry.DESCRIPTION_CLMN + ","
                                    + ArticleEntry.COMMENT_CLMN + ","
                                    + ArticleEntry.LINK_CLMN + ","
                                    + ArticleEntry.IMG_LINK_CLMN + ","
                                    + ArticleEntry.PUB_DATE_CLMN + ","
                                    + ArticleEntry.FEED_CLMN + ","
                                    + ArticleEntry.SCORE_CLMN + ","
                                    + ArticleEntry.READ_CLMN
                                    + ") VALUES(?,?,?,?,?,?,?,?,?,"
                                    + "(SELECT " + ArticleEntry.READ_CLMN + " FROM " + ArticleEntry.TABLE_NAME + " WHERE " + ArticleEntry.HASH_ID_CLMN + " = '" + article.getHashId() + "'))",
                            new Object[]{
                                    article.getHashId(),
                                    article.getTitle(),
                                    article.getDescription(),
                                    article.getComment(),
                                    article.getLink() == null ? "" : article.getLink(),
                                    article.getImgLink(),
                                    // if the date is null set the date as 01/01/2019
                                    article.getPubDate() == null ? sdf.format(new Date(1546297200000L)) : sdf.format(article.getPubDate()),
                                    article.getFeed(),
                                    article.getScore()});
                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Skipped article from saving to local DB: " + article);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public void delete(Article article) throws SQLException {
        db.execSQL("DELETE FROM " + ArticleEntry.TABLE_NAME + " WHERE " +
                        ArticleEntry.HASH_ID_CLMN + " = ?",
                new Object[]{article.getHashId()});
    }

    public void deleteAll() throws SQLException {
        db.execSQL("DELETE FROM " + ArticleEntry.TABLE_NAME + " WHERE 1");
    }

    public ArticleCursor findAll(int orderBy) {
        Cursor res;
        if (orderBy == UserData.ORDER_BY_DATE) {
            // order by read, datetime (by "YYYY-MM-DD HH:MM:SS")
            res = db.rawQuery("SELECT * FROM " +
                    ArticleEntry.TABLE_NAME + " ORDER BY "
                    + ArticleEntry.READ_CLMN + " ASC, "
                    + "datetime(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC;", null);
        } else {
            // orderBy == UserData.ORDER_BY_RATING)
            // order by read, date ("YYYY-MM-DD"), rating
            res = db.rawQuery("SELECT * FROM " +
                    ArticleEntry.TABLE_NAME + " ORDER BY "
                    + ArticleEntry.READ_CLMN + " ASC, "
                    + "date(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC,"
                    + ArticleEntry.SCORE_CLMN + " DESC,"
                    + "datetime(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC;", null);
        }

        return new ArticleCursor(res);
    }

    public ArticleCursor findAllFiltered(int orderBy, String feedIds) {
        Cursor res;
        if (orderBy == UserData.ORDER_BY_DATE) {
            // order by read, datetime (by "YYYY-MM-DD HH:MM:SS")
            res = db.rawQuery("SELECT * FROM " + ArticleEntry.TABLE_NAME
                    + " WHERE " + ArticleEntry.FEED_CLMN + " IN (" + feedIds + ")"
                    + " ORDER BY "
                    + ArticleEntry.READ_CLMN + " ASC, "
                    + "datetime(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC;", null);
        } else {
            // orderBy == UserData.ORDER_BY_RATING)
            // order by read, date ("YYYY-MM-DD"), rating, datetime (by "YYYY-MM-DD HH:MM:SS")
            res = db.rawQuery("SELECT * FROM " + ArticleEntry.TABLE_NAME
                    + " WHERE " + ArticleEntry.FEED_CLMN + " IN (" + feedIds + ")"
                    + " ORDER BY "
                    + ArticleEntry.READ_CLMN + " ASC, "
                    + "date(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC,"
                    + ArticleEntry.SCORE_CLMN + " DESC,"
                    + "datetime(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC;", null);
        }

        return new ArticleCursor(res);
    }

    public ArticleCursor findById(long id) {
        Cursor res = db.rawQuery("SELECT * FROM " +
                ArticleEntry.TABLE_NAME + " WHERE " +
                ArticleEntry.HASH_ID_CLMN + " = ?", new String[]{String.valueOf(id)});
        return new ArticleCursor(res);
    }

    public void setRead(String hashId) {
        db.execSQL("UPDATE " + ArticleEntry.TABLE_NAME
                        + " SET " + ArticleEntry.READ_CLMN + " = ?"
                        + " WHERE " + ArticleEntry.HASH_ID_CLMN + " = ?;",
                new Object[]{1, hashId});
    }

    public boolean getRead(String hashId) {
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + ArticleEntry.READ_CLMN + " FROM " +
                    ArticleEntry.TABLE_NAME + " WHERE " +
                    ArticleEntry.HASH_ID_CLMN + " = ?", new String[]{hashId});
            if (res.getCount() != 0) {
                res.moveToNext();
                int read = res.getInt(0);
                return read == 1;
            } else {
                return false;
            }
        } finally {
            if (res != null)
                res.close();
        }
    }

    public void deleteOldArticles(List<Feed> feedList) {
        for (Feed feed : feedList) {
            db.execSQL("DELETE FROM " + ArticleEntry.TABLE_NAME
                            + " WHERE " + ArticleEntry.HASH_ID_CLMN
                            + " NOT IN (SELECT " + ArticleEntry.HASH_ID_CLMN
                            + " FROM "
                            + "(SELECT " + ArticleEntry.HASH_ID_CLMN
                            + " FROM " + ArticleEntry.TABLE_NAME
                            + " WHERE " + ArticleEntry.FEED_CLMN + " = ?"
                            + " ORDER BY datetime(" + ArticleEntry.PUB_DATE_CLMN + ")" + " DESC"
                            + " LIMIT " + MAX_ARTICLES_TO_KEEP + ") tab );",
                    new Object[]{feed.getId()});
        }
    }

    public Article getArticle(String articleHashId) {
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT * FROM " +
                    ArticleEntry.TABLE_NAME + " WHERE " +
                    ArticleEntry.HASH_ID_CLMN + " = ?", new String[]{articleHashId});
            if (res.getCount() != 0) {
                res.moveToNext();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                ArticleCursor cursor = new ArticleCursor(res);
                Article article = new Article();
                article.setHashId(cursor.getHashId());
                article.setTitle(cursor.getTitle());
                article.setDescription(cursor.getDescription());
                article.setComment(cursor.getComment());
                article.setLink(cursor.getLink());
                article.setImgLink(cursor.getImageLink());
                article.setPubDate(sdf.parse(cursor.getPubDate()));
                article.setFeed(cursor.getFeed());
                article.setScore(cursor.getScore());
                return article;
            } else {
                return null;
            }
        } catch (Exception e){
            Log.e("RSSLOG", e.toString());
            return  null;
        }
        finally {
            if (res != null)
                res.close();
        }
    }
}
