package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Feed implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("subscribed")
    @Expose
    private int subscribed;

    @SerializedName("content_type")
    @Expose
    private String contentType;

    @SerializedName("cover_url")
    @Expose
    private String coverURL;

    @SerializedName("icon_url")
    @Expose
    private String iconURL;

    @SerializedName("visual_url")
    @Expose
    private String visualURL;

    @SerializedName("logo_url")
    @Expose
    private String logoURL;

    @SerializedName("cover_color")
    @Expose
    private String coverColor;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("lang")
    @Expose
    private String lang;

    @SerializedName("popularity")
    @Expose
    private int popularity;

    private Multifeed multifeed;

    public Feed(){

    }

    public Feed(int id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }

    public Feed(int id, String title, String description, String link, String website, int subscribed, String contentType, String coverURL, String iconURL, String visualURL, String logoURL, String coverColor, String category, String lang, int popularity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.website = website;
        this.subscribed = subscribed;
        this.contentType = contentType;
        this.coverURL = coverURL;
        this.iconURL = iconURL;
        this.visualURL = visualURL;
        this.logoURL = logoURL;
        this.coverColor = coverColor;
        this.category = category;
        this.lang = lang;
        this.popularity = popularity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(int subscribed) {
        this.subscribed = subscribed;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getVisualURL() {
        return visualURL;
    }

    public void setVisualURL(String visualURL) {
        this.visualURL = visualURL;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getCoverColor() {
        return coverColor;
    }

    public void setMultifeed(Multifeed multifeed) {
        this.multifeed = multifeed;
    }

    public Multifeed getMultifeed(){
        return this.multifeed;
    }

    public void setCoverColor(String coverColor) {
        this.coverColor = coverColor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", website='" + website + '\'' +
                ", subscribed=" + subscribed +
                ", contentType='" + contentType + '\'' +
                ", coverURL='" + coverURL + '\'' +
                ", iconURL='" + iconURL + '\'' +
                ", visualURL='" + visualURL + '\'' +
                ", logoURL='" + logoURL + '\'' +
                ", coverColor='" + coverColor + '\'' +
                ", category='" + category + '\'' +
                ", lang='" + lang + '\'' +
                ", popularity=" + popularity +
        '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (id != feed.id) return false;
        if (subscribed != feed.subscribed) return false;
        if (popularity != feed.popularity) return false;
        if (!title.equals(feed.title)) return false;
        if (description != null ? !description.equals(feed.description) : feed.description != null)
            return false;
        if (!link.equals(feed.link)) return false;
        if (!website.equals(feed.website)) return false;
        if (contentType != null ? !contentType.equals(feed.contentType) : feed.contentType != null)
            return false;
        if (coverURL != null ? !coverURL.equals(feed.coverURL) : feed.coverURL != null)
            return false;
        if (!iconURL.equals(feed.iconURL)) return false;
        if (!visualURL.equals(feed.visualURL)) return false;
        if (logoURL != null ? !logoURL.equals(feed.logoURL) : feed.logoURL != null) return false;
        if (coverColor != null ? !coverColor.equals(feed.coverColor) : feed.coverColor != null)
            return false;
        if (!category.equals(feed.category)) return false;
        return lang.equals(feed.lang);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + link.hashCode();
        result = 31 * result + website.hashCode();
        result = 31 * result + subscribed;
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (coverURL != null ? coverURL.hashCode() : 0);
        result = 31 * result + iconURL.hashCode();
        result = 31 * result + visualURL.hashCode();
        result = 31 * result + (logoURL != null ? logoURL.hashCode() : 0);
        result = 31 * result + (coverColor != null ? coverColor.hashCode() : 0);
        result = 31 * result + category.hashCode();
        result = 31 * result + lang.hashCode();
        result = 31 * result + popularity;
        return result;
    }

    public static List<Feed> generateMockupFeeds(int length) {
        Random random = new Random();
        Lorem lorem = LoremIpsum.getInstance();
        List<Feed> feeds = new ArrayList<Feed>();
        for (int i = 0; i < length; i++) {
            feeds.add(new Feed(random.nextInt(), lorem.getName(), lorem.getCity()));
        }
        return feeds;
    }
}
