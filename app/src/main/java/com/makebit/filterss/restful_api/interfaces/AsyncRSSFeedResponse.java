package com.makebit.filterss.restful_api.interfaces;

import com.makebit.filterss.models.RSSFeed;

public interface AsyncRSSFeedResponse {
    void processFinish(Object output, RSSFeed rssFeed);
}
