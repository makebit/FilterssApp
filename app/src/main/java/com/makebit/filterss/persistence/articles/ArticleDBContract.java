package com.makebit.filterss.persistence.articles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class ArticleDBContract {

    private static final String DATABASE_NAME = "rssfeedrepository.db";

    private static final String SQL_CREATE_ARTICLE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ ArticleEntry.TABLE_NAME+ " ("+
                    ArticleEntry.HASH_ID_CLMN       +" varchar(40) PRIMARY KEY, "+
                    ArticleEntry.TITLE_CLMN         +" varchar(255) NOT NULL, "+
                    ArticleEntry.DESCRIPTION_CLMN   +" text, "+
                    ArticleEntry.COMMENT_CLMN       +" text, "+
                    ArticleEntry.LINK_CLMN          +" text NOT NULL, "+
                    ArticleEntry.IMG_LINK_CLMN      +" text, "+
                    ArticleEntry.PUB_DATE_CLMN      +" datetime, "+
                    ArticleEntry.FEED_CLMN          +" int(11) NOT NULL, "+
                    ArticleEntry.SCORE_CLMN         +" float, " +
                    ArticleEntry.READ_CLMN               +" int(1) DEFAULT 0); " +
                    "CREATE INDEX hashid_index ON " + ArticleEntry.TABLE_NAME + " (" + ArticleEntry.HASH_ID_CLMN + ");";

    public static SQLiteDatabase getWritableDatabase(Context context){
        return new ArticleDBHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context){
        return new ArticleDBHelper(context).getReadableDatabase();
    }

    /**
     * Article Entry Class: contains all the names used for columns in the table
     */
    public static class ArticleEntry implements BaseColumns {
        public static final String TABLE_NAME       = "article";
        public static final String HASH_ID_CLMN     = "hash_id";
        public static final String TITLE_CLMN       = "title";
        public static final String DESCRIPTION_CLMN = "description";
        public static final String COMMENT_CLMN     = "comment";
        public static final String LINK_CLMN        = "link";
        public static final String IMG_LINK_CLMN    = "img_link";
        public static final String PUB_DATE_CLMN    = "pub_date";
        public static final String FEED_CLMN        = "feed";
        public static final String SCORE_CLMN       = "score";
        public static final String READ_CLMN        = "read";
    }

    /**
     * Article DB Helper Class: is a SQLiteOpenHelper extended class
     */
    private static class ArticleDBHelper extends SQLiteOpenHelper {

        public ArticleDBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}