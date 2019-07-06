package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReadArticle implements Serializable {
    @SerializedName("user")
    @Expose
    private int user;

    @SerializedName("article")
    @Expose
    private long article;

    @SerializedName("vote")
    @Expose
    private float vote;

    public ReadArticle(){

    }

    public ReadArticle(int user, long article, float vote) {
        this.user = user;
        this.article = article;
        this.vote = vote;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public long getArticle() {
        return article;
    }

    public void setArticle(long article) {
        this.article = article;
    }

    public float getVote() {
        return vote;
    }

    public void setVote(float vote) {
        this.vote = vote;
    }

    @Override
    public String toString() {
        return "ReadArticle{" +
                "user=" + user +
                ", article=" + article +
                ", vote=" + vote +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadArticle that = (ReadArticle) o;

        if (user != that.user) return false;
        if (article != that.article) return false;
        return Float.compare(that.vote, vote) == 0;
    }

    @Override
    public int hashCode() {
        int result = user;
        result = 31 * result + (int) (article ^ (article >>> 32));
        result = 31 * result + (vote != +0.0f ? Float.floatToIntBits(vote) : 0);
        return result;
    }
}
