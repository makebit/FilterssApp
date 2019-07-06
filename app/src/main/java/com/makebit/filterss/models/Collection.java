package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Collection implements Serializable{
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("color")
    @Expose
    private int color;

    @SerializedName("articles")
    @Expose
    private List<Article> articles;

    public Collection() {
    }

    public Collection(int id, String title, int color) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.articles = new ArrayList<>();
    }

    public Collection(int id, String title, int color, List<Article> articles) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.articles = articles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color=" + color +
                ", articles=" + articles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;

        if (id != that.id) return false;
        if (color != that.color) return false;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + color;
        return result;
    }

    /*
    public static List<Collection> generateMockupCollections(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        List<Collection> collections = new ArrayList<Collection>(length);
        for (int i = 0; i < length; i++) {
            collections.add(new Collection(
                    random.nextInt(),
                    lorem.getName(),
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))));
            collections.get(i).setArticles(Article.generateMockupArticles(10));
        }
        return collections;
    }
    */

    public static String[] toStrings(List<Collection> collections) {
        String[] collectionsStrings = new String[collections.size()];
        for (int i = 0; i < collections.size(); i++) {
            collectionsStrings[i] = collections.get(i).getTitle();
        }
        return collectionsStrings;
    }
}
