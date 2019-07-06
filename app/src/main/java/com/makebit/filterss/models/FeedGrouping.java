package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedGrouping implements Serializable {
    @SerializedName("feed")
    @Expose
    private int feed;

    @SerializedName("multifeed")
    @Expose
    private int multifeed;

    @SerializedName("article_checkpoint")
    @Expose
    private long articleCheckpoint;

    public FeedGrouping(){

    }

    public FeedGrouping(int feed, int multifeed, long articleCheckpoint) {
        this.feed = feed;
        this.multifeed = multifeed;
        this.articleCheckpoint = articleCheckpoint;
    }

    public int getFeed() {
        return feed;
    }

    public void setFeed(int feed) {
        this.feed = feed;
    }

    public int getMultifeed() {
        return multifeed;
    }

    public void setMultifeed(int multifeed) {
        this.multifeed = multifeed;
    }

    public long getArticleCheckpoint() {
        return articleCheckpoint;
    }

    public void setArticleCheckpoint(long articleCheckpoint) {
        this.articleCheckpoint = articleCheckpoint;
    }

    @Override
    public String toString() {
        return "FeedGrouping{" +
                "feed=" + feed +
                ", multifeed=" + multifeed +
                ", articleCheckpoint=" + articleCheckpoint +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedGrouping that = (FeedGrouping) o;

        if (feed != that.feed) return false;
        if (multifeed != that.multifeed) return false;
        return articleCheckpoint == that.articleCheckpoint;
    }

    @Override
    public int hashCode() {
        int result = feed;
        result = 31 * result + multifeed;
        result = 31 * result + (int) (articleCheckpoint ^ (articleCheckpoint >>> 32));
        return result;
    }
}
