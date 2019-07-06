package com.makebit.filterss.rss_parser;

import android.util.Log;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.RSSFeed;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parses an RSS feed and adds the information to a new
 * RSSFeed object. Has the ability to report progress to a
 * ProgressBar if one is passed to the constructor.
 */
public class DOMParser {
    private final String TAG = getClass().getName();

    //TITLE:"title"
    private ArrayList<String> titleList = new ArrayList<String>() {{
        add("title");
    }};
    //DESCRIPTION:"description", "content:encoded", "content", "summary"(if no other)
    private ArrayList<String> descriptionList = new ArrayList<String>() {{
        add("description");
        add("content:encoded");
        add("content");
        add("summary");
    }};
    //PUB_DATE:"pubDate", "published", "dc:date", "a10:updated"
    private ArrayList<String> pubDateList = new ArrayList<String>() {{
        add("pubDate");
        add("published");
        add("dc:date");
        add("a10:updated");
    }};
    //AUTHOR:"author", "dc:creator", "itunes:author"
    private ArrayList<String> authorList = new ArrayList<String>() {{
        add("author");
        add("dc:creator");
        add("itunes:author");
    }};
    //ARTICLE_LINK:"link"
    private ArrayList<String> linkList = new ArrayList<String>() {{
        add("link");
        add("id");
    }};
    //THUMBNAIL_IMAGE_LINK:"thumbnail", "thumb"
    private ArrayList<String> thumbnailList = new ArrayList<String>() {{
        add("thumbnail");
        add("thumb");
        add("media:content");
    }};
    //COMMENTS_LINK:"comments", "wfw:commentRss"(rss-comments, use if no normal link for comments)
    private ArrayList<String> commentsList = new ArrayList<String>() {{
        add("comments");
        add("wfw:commentRss");
    }};

    //SingleTAG
    //THUMBNAIL_IMAGE_LINK:"media:thumbnail"(url=), "media:content"(url=), "enclosure"(url=)
    private ArrayList<String> thumbnailSingleTagList = new ArrayList<String>() {{
        add("media:thumbnail");
        add("media:content");
        add("enclosure");
        //add("enclosure");
    }};

    // Create a new RSS feed
    private RSSFeed feed = new RSSFeed();

    public RSSFeed parseXML(String feedURL) {
        try {
            // Create a new DocumentBuilder
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            //Create an URL Connection
            String url = feedURL;

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setReadTimeout(10000);

            boolean firstCheck = true;
            boolean redirect = false;
            int maxTries = 3;
            int tries = 0;
            String log = "Parsing: ";


            while ((firstCheck || redirect) && tries < maxTries) {
                firstCheck = false;
                redirect = false;

                int status = urlConnection.getResponseCode();
                log += url + " status " + status;

                // normally, 3xx is redirect
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER) {
                        redirect = true;
                    } else if (status == HttpURLConnection.HTTP_BAD_REQUEST
                            || status == HttpURLConnection.HTTP_NOT_FOUND) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, log);
                        throw new Exception(url + " error: HTTP 400 | 404");
                    }
                }

                if (redirect) {
                    // get redirect url from "location" header field
                    url = urlConnection.getHeaderField("Location");
                    // open the new connnection again
                    urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    urlConnection.setReadTimeout(5000);

                    log += "... redirect to: " + url + ". ";
                }
                maxTries++;
            }

            Log.d(ArticleActivity.logTag + ":" + TAG, log);


            // Parse the XML
            //Document doc = builder.parse(new InputSource(url.openStream()));
            Document doc = builder.parse(new InputSource(urlConnection.getInputStream()));

            // Normalize the data
            doc.getDocumentElement().normalize();

            // Get all <item> OR <entry> tags.
            NodeList list = doc.getElementsByTagName("item");

            if (list.getLength() == 0)
                list = doc.getElementsByTagName("entry");

            // Get size of the list
            int length = list.getLength();

            //Measure the execution time of the parsing
            long startTime = System.nanoTime();

            // For all the items in the feed
            for (int i = 0; i < length; i++) {
                // Create a new node of the first item
                Node currentNode = list.item(i);
                // Create a new RSS item
                Article article = new Article();

                // Get the child nodes of the first item
                NodeList nodeChild = currentNode.getChildNodes();
                // Get size of the child list
                int cLength = nodeChild.getLength();

                // For all the children of a node
                for (int j = 0; j < cLength; j++) {
                    // Get the name of the child
                    String nodeName = nodeChild.item(j).getNodeName(), nodeString = null;
                    // If there is at least one child element
                    if (nodeChild.item(j).getFirstChild() != null) {
                        // Set the string to be the value of the node
                        nodeString = nodeChild.item(j).getFirstChild().getNodeValue();
                    }

                    // If the node string value isn't null
                    if (nodeString != null) {
                        // Set the Title
                        if (titleList.contains(nodeName)) {
                            //if(article.getTitle() == null)
                            article.setTitle(nodeString);
                        }
                        // Set the Description ("description", "content:encoded", "content", "summary"(if no other))
                        else if (descriptionList.contains(nodeName)) {
                            if (article.getDescription() == null || article.getDescription().length() < nodeString.length())
                                article.setDescription(nodeString);//.replaceAll("\\<[^>]*>", ""));
                        }
                        // Set the PublicationDate ("pubDate", "published", "dc:date", "a10:updated")
                        else if (pubDateList.contains(nodeName)) {
                            if (article.getPubDate() == null) {
                                Date pubDate = parseDateFromString(nodeString);
                                article.setPubDate(pubDate);
                            }
                        }
                        // Set the Article's Link
                        else if (linkList.contains(nodeName)) {
                            //if (article.getLink() == null)
                            article.setLink(nodeString);
                        }
                        // Set the Thumbnail/ImageLink  ("thumbnail", "thumb")
                        else if (thumbnailList.contains(nodeName)) {
                            //if (article.getImgLink() == null)

                            // get url from media:content
                            String imageUrl = null;
                            try {
                                if(nodeChild.item(j).getAttributes().getNamedItem("url") != null)
                                    imageUrl = nodeChild.item(j).getAttributes().getNamedItem("url").getNodeValue();
                            } catch (Exception e) {
                                Log.e(ArticleActivity.logTag + ":" + TAG, "EXCEPTION thumbnailList URL TAG" + e + " on " + feedURL);
                            }
                            if (imageUrl != null)
                                article.setImgLink(imageUrl);
                            else
                                article.setImgLink(nodeString);
                        }
                        // Set the Article's Comments   ("comments", "wfw:commentRss")
                        else if (commentsList.contains(nodeName)) {
                            if (article.getComment() == null)
                                article.setComment(nodeString);
                        }
                    }
                    // Self closing TAGs with no node string value
                    else {
                        if (article.getImgLink() == null || article.getImgLink().isEmpty()) {
                            // Set the Thumbnail/ImageLink  ("media:thumbnail"(url=), "media:content"(url=), "enclosure"(url=))
                            if (thumbnailSingleTagList.contains(nodeName)) {
                                if (nodeChild.item(j).getAttributes().getNamedItem("url") != null)
                                    article.setImgLink(nodeChild.item(j).getAttributes().getNamedItem("url").getNodeValue());
                            } else {
                                // catch media:content
                                try {
                                    if (nodeChild.item(j).getFirstChild() != null && nodeChild.item(j).getFirstChild().getNodeName().equals("media:content")) {
                                        if (nodeChild.item(j).getFirstChild().getAttributes().getNamedItem("url").getNodeValue() != null)
                                            article.setImgLink(nodeChild.item(j).getFirstChild().getAttributes().getNamedItem("url").getNodeValue());
                                    }
                                } catch (Exception e) {
                                    Log.e(ArticleActivity.logTag + ":" + TAG, "EXCEPTION thumbnailSingleTagList URL TAG" + e + " on " + feedURL);
                                }
                            }
                        }
                    }
                }
                article.computeHashId();
                // Add the new item to the RSS feed
                feed.addItem(article);
            }
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            System.out.println("DOM_Parsing Duration(" + feedURL + "):  " + duration / 1000000 + "ms");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(ArticleActivity.logTag + ":" + TAG, "EXCEPTION: " + e.getMessage() + " on " + feedURL);

        }
        // Return the feed
        return feed;
    }


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
}
