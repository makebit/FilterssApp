package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SavedArticle implements Serializable {
    @SerializedName("article")
    @Expose
    private long article;

    @SerializedName("collection")
    @Expose
    private int collection;

    public SavedArticle(){

    }

    public SavedArticle(long article, int collection) {
        this.article = article;
        this.collection = collection;
    }

    public long getArticle() {
        return article;
    }

    public void setArticle(long article) {
        this.article = article;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return "SavedArticle{" +
                "article=" + article +
                ", collection=" + collection +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedArticle that = (SavedArticle) o;

        if (article != that.article) return false;
        return collection == that.collection;
    }

    @Override
    public int hashCode() {
        int result = (int) (article ^ (article >>> 32));
        result = 31 * result + collection;
        return result;
    }
}
