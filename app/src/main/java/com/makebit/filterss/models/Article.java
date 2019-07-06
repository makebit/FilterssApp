package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Article implements Serializable {
    @SerializedName("hash_id")
    @Expose
    private String hashId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("img_link")
    @Expose
    private String imgLink;

    @SerializedName("pub_date")
    @Expose
    private Date pubDate;

    @SerializedName("feed")
    @Expose
    private int feed;

    // LOCAL FIELDS
    private Feed feedObj;
    private float score = 1;
    private boolean read; // local field to know if the article was read. The info is store in the local db

    public Article() {
    }

    public Article(String hashId, String title, String description, String link, Date pubDate, String imgLink) {
        this.hashId = hashId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.imgLink = imgLink;
        this.pubDate = pubDate;
        this.read = false;
    }

    public Article(String hashId, String title, String description, String comment, String link, String imgLink, Date pubDate, int feedId) {
        this.hashId = hashId;
        this.title = title;
        this.description = description;
        this.comment = comment;
        this.link = link;
        this.imgLink = imgLink;
        this.pubDate = pubDate;
        this.feed = feedId;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    /**
     * Compute the HashID locally using the same Hashing function used on the server(CRC64 ECMA182).
     * The string being hashed is the Article's URL.
     *
     * @return HashId newly generated
     */
    public String computeHashId() {
        this.hashId = byteArrayToHexString(DigestUtils.sha(this.getTitle() + this.getLink()));
        return this.hashId;
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        if(this.description != null){
            String desc = StringEscapeUtils.unescapeHtml(this.description.replaceAll("\\<[^>]*>|\\n", "").trim());
            return desc != null ? desc.substring(0, desc.length() < 180 ? desc.length() : 180) : null;
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Feed getFeedObj() {
        return feedObj;
    }

    public void setFeedObj(Feed feedO) {
        this.feedObj = feedO;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setFeed(int feed) {
        this.feed = feed;
    }

    public int getFeed() {
        return feed;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    @Override
    public String toString() {
        return "Article{" +
                "score=" + score +
                ", hashId=" + hashId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", link='" + link + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", pubDate=" + pubDate +
                ", feed='" + feed + '\'' +
                ", feedObj='" + feedObj + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (hashId != article.hashId) return false;
        if (feed != article.feed) return false;
        if (!title.equals(article.title)) return false;
        if (description != null ? !description.equals(article.description) : article.description != null)
            return false;
        if (comment != null ? !comment.equals(article.comment) : article.comment != null)
            return false;
        if (link != null ? !link.equals(article.link) : article.link != null) return false;
        if (imgLink != null ? !imgLink.equals(article.imgLink) : article.imgLink != null)
            return false;
        return (pubDate != null ? !pubDate.equals(article.pubDate) : article.pubDate != null);
    }

    @Override
    public int hashCode() {
        int result = result = title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (imgLink != null ? imgLink.hashCode() : 0);
        result = 31 * result + (pubDate != null ? pubDate.hashCode() : 0);
        result = 31 * result + feed;
        return result;
    }

    public int getReadingTime() {
        if (this.description == null || this.description.isEmpty()) {
            return 0;
        }

        StringTokenizer tokens = new StringTokenizer(this.description);
        return tokens.countTokens() / 130; // 130 is the avg words read per minute
    }

    /*
    private static Article createMockArticle() {
        Random random = new Random();
        int imageId = random.nextInt(1085);
        return new Article(random.nextInt(), makeTitle(), makeBody(), makeSource(), new Date(), makeImage(imageId));
    }

    private static String makeSource() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getName();
    }

    private static String makeBody() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getParagraphs(1, 1);
    }

    private static String makeTitle() {
        Lorem lorem = LoremIpsum.getInstance();
        return lorem.getTitle(1, 18);
    }

    private static String makeThumbnail(int imageId){
        Random random = new Random();
        return "https://picsum.photos/200/300?image=" + imageId;
    }

    private static String makeImage(int imageId){
        Random random = new Random();
        return "https://picsum.photos/2000/3000?image=" + imageId;
    }

    public static List<Article> generateMockupArticles(int length) {
        List<Article> articles = new ArrayList<Article>(length);
        for (int i = 0; i < length; i++) {
            articles.add(createMockArticle());
        }
        return articles;
    } */

    /**
     * Parse a Date from a String using multiple matching patterns(Natty library)
     *
     * @param candidate String to parse
     * @return Date object or null if the parsing failed
     */
    private Date parseDateFromString(String candidate) {
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(candidate);
        for (DateGroup group : groups) {
            List<Date> dates = group.getDates();
            return dates.get(0);
        }
        return null;
    }

    /**
     * Find out if this article is present in the passed List of articles
     *
     * @param articleList
     * @return True if article is present in the list, false otherwise
     */
    public synchronized boolean isArticleInTheList(List<Article> articleList) {
        for (Article article : articleList) {
            if (hashId == article.hashId)
                return true;
        }
        return false;
    }

    /**
     * Check if the given url is a valid one
     *
     * @param url
     * @return
     */
    public static final boolean checkUrlIsValid(String url) {
        if (url != null && !url.isEmpty()) {
            try {
                new URL(url);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

}

