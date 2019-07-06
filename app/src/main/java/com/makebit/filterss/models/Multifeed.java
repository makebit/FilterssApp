package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Multifeed implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("color")
    @Expose
    private int color;

    @SerializedName("rating")
    @Expose
    private int rating;

    @SerializedName("feeds")
    @Expose
    private List<Feed> feeds;

    public Multifeed() {
    }

    public Multifeed(int id, String title, int color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    public Multifeed(int id, String title, int color, List<Feed> feeds) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.feeds = feeds;
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

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Multifeed{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color=" + color +
                ", rating=" + rating +
                ", feeds=" + feeds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Multifeed multifeed = (Multifeed) o;

        if (id != multifeed.id) return false;
        if (color != multifeed.color) return false;
        return title.equals(multifeed.title);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + color;
        return result;
    }

    /*
    public static List<Multifeed> generateMockupMultifeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        List<Multifeed> multifeeds = new ArrayList<Multifeed>(length);
        for (int i = 0; i < length; i++) {
            multifeeds.add(new Multifeed(
                    random.nextInt(),
                    lorem.getName(),
                    random.nextInt(5),
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))));
            multifeeds.get(i).setFeeds(Feed.generateMockupFeeds(random.nextInt(10)));
        }

        return multifeeds;
    }*/

    public static String[] toStrings(List<Multifeed> multifeeds) {
        String[] multifeedsStrings = new String[multifeeds.size()];
        for (int i = 0; i < multifeeds.size(); i++) {
            multifeedsStrings[i] = multifeeds.get(i).getTitle();
        }
        return multifeedsStrings;
    }
}
