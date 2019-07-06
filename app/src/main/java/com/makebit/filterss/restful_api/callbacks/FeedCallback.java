package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.Feed;

import java.util.List;

public interface FeedCallback {
    public void onLoad(List<Feed> feeds);
    public void onFailure();
}
